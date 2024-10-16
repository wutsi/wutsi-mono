package com.wutsi.blog.story.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.STORY_ATTACHMENT_DOWNLOADED_EVENT
import com.wutsi.blog.event.EventType.STORY_CREATED_EVENT
import com.wutsi.blog.event.EventType.STORY_DELETED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORT_FAILED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLICATION_SCHEDULED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UPDATED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.story.dao.ReaderRepository
import com.wutsi.blog.story.dao.SearchStoryQueryBuilder
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryAttachmentDownloadedEventPayload
import com.wutsi.blog.story.dto.StoryCreatedEventPayload
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryPublicationScheduledEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryStatus.DRAFT
import com.wutsi.blog.story.dto.StoryStatus.PUBLISHED
import com.wutsi.blog.story.dto.StoryUpdatedEventPayload
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.story.dto.WPPValidation
import com.wutsi.blog.story.dto.WebPage
import com.wutsi.blog.story.exception.ImportException
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.blog.util.Predicates
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityResult
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.error.exception.WutsiException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import jakarta.persistence.EntityManager
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.HttpStatusException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import java.net.URL
import java.time.Clock
import java.util.Date
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Service
class StoryService(
    private val clock: Clock,
    private val storyDao: StoryRepository,
    private val storyContentDao: StoryContentRepository,
    private val kpiMonthlyDao: StoryKpiRepository,
    private val subscriptionDao: SubscriptionRepository,
    private val editorjs: EditorJSService,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val mapper: StoryMapper,
    private val tagService: TagService,
    private val scraperService: WebScaperService,
    private val userService: UserService,
    private val eventStream: EventStream,
    private val eventStore: EventStore,
    private val readerDao: ReaderRepository,
    private val wppService: WPPService,
    private val summaryGenerator: StorySummaryGenerator,
    private val tagExtractor: StoryTagExtractor,
    private val storySearchFilter: StorySearchFilterSet,

    @Value("\${wutsi.website.url}") private val websiteUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryService::class.java)
        private const val WORDS_PER_MINUTES = 250
        const val SUMMARY_MAX_LEN = 200
    }

    @Transactional
    fun onAttachmentDownloaded(payload: StoryAttachmentDownloadedEventPayload) {
        logger.add("payload_story_id", payload.storyId)
        logger.add("payload_user_id", payload.userId)
        logger.add("payload_filename", payload.filename)
        logger.add("payload_timestamp", payload.timestamp)

        val story = storyDao.findById(payload.storyId).getOrNull() ?: return

        // Store notification
        notify(
            type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
            payload.storyId,
            userId = payload.userId,
            timestamp = payload.timestamp,
            payload = payload,
        )

        // Update download count
        story.attachmentDownloadCount = eventStore.eventCount(
            streamId = StreamId.STORY,
            entityId = payload.storyId.toString(),
            type = STORY_ATTACHMENT_DOWNLOADED_EVENT,
        )
        story.modificationDateTime = Date()
        storyDao.save(story)

        // Subscribe user to blog :-)
        if (payload.userId != null && payload.subscribe) {
            eventStream.enqueue(
                type = SUBSCRIBE_COMMAND,
                payload = SubscribeCommand(
                    userId = story.userId,
                    subscriberId = payload.userId!!,
                    timestamp = payload.timestamp,
                ),
            )
        }
    }

    @Transactional
    fun onStoryLiked(story: StoryEntity) {
        val count = updateLikeCount(story)
        logger.add("like_count", count)
    }

    @Transactional
    fun onStoryUnliked(story: StoryEntity) {
        val count = updateLikeCount(story)
        logger.add("like_count", count)
    }

    @Transactional
    fun onDailyEmailSent(story: StoryEntity) {
        story.recipientCount = eventStore.eventCount(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_DAILY_EMAIL_SENT_EVENT,
        )
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    private fun updateLikeCount(story: StoryEntity): Long {
        story.likeCount = java.lang.Long.max(
            0L,
            count(StreamId.LIKE, story, EventType.STORY_LIKED_EVENT) - count(
                StreamId.LIKE,
                story,
                EventType.STORY_UNLIKED_EVENT,
            ),
        )
        story.modificationDateTime = Date()
        storyDao.save(story)
        return story.likeCount
    }

    private fun count(streamId: Long, story: StoryEntity, type: String): Long =
        eventStore.eventCount(streamId = streamId, entityId = story.id.toString(), type = type)

    @Transactional
    fun onStoryShared(story: StoryEntity) {
        story.shareCount = count(StreamId.SHARE, story, EventType.STORY_SHARED_EVENT)
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    fun findById(id: Long): StoryEntity {
        val story = storyDao
            .findById(id)
            .orElseThrow { NotFoundException(Error(ErrorCode.STORY_NOT_FOUND)) }

        if (story.deleted) {
            throw NotFoundException(Error(ErrorCode.STORY_NOT_FOUND))
        }
        return story
    }

    fun findContent(story: StoryEntity, language: String?): Optional<StoryContentEntity> =
        storyContentDao.findByStoryAndLanguage(story, language)

    @Transactional
    fun onKpisImported(story: StoryEntity) {
        story.readCount = kpiMonthlyDao.sumValueByStoryIdAndTypeAndSource(
            story.id ?: -1,
            KpiType.READ,
            TrafficSource.ALL,
        ) ?: 0
        story.subscriberCount = kpiMonthlyDao.sumValueByStoryIdAndTypeAndSource(
            story.id ?: -1,
            KpiType.SUBSCRIPTION,
            TrafficSource.ALL,
        ) ?: 0
        story.totalDurationSeconds = kpiMonthlyDao.sumValueByStoryIdAndTypeAndSource(
            story.id ?: -1,
            KpiType.DURATION,
            TrafficSource.ALL,
        ) ?: 0
        story.subscriberReaderCount = readerDao.countSubscriberByStoryIdAndUserId(story.id!!, story.userId) ?: 0L
        story.emailReaderCount = readerDao.countByStoryIdAndEmail(story.id, true) ?: 0L
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    @Transactional
    fun updateReaderCount(id: Long, count: Long) {
        val story = storyDao.findById(id).getOrNull() ?: return

        story.readerCount = count
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    @Transactional
    fun updateClickCount(id: Long, count: Long) {
        val story = storyDao.findById(id).getOrNull() ?: return

        story.clickCount = count
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    @Transactional
    fun create(command: CreateStoryCommand): StoryEntity {
        logger.add("request_user_id", command.userId)
        logger.add("request_title", command.title)
        logger.add("request_content", command.content?.take(100))
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "CreateStoryCommand")

        val story = execute(command)

        val payload = StoryCreatedEventPayload(
            title = command.title,
            content = command.content,
        )
        notify(STORY_CREATED_EVENT, story.id!!, command.userId, command.timestamp, payload)
        return story
    }

    @Transactional
    fun onCreated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyDao.findById(event.entityId.toLong()).get()

        userService.onStoryCreated(story)
    }

    private fun execute(command: CreateStoryCommand): StoryEntity {
        val now = Date(command.timestamp)
        val doc = editorjs.fromJson(command.content)
        val story = createStory(command, doc, now)
        createContent(command, story, now)
        return story
    }

    private fun createStory(command: CreateStoryCommand, doc: EJSDocument, now: Date): StoryEntity {
        val summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN)
        val wordCount = editorjs.wordCount(doc)
        return storyDao.save(
            StoryEntity(
                userId = command.userId!!,
                title = command.title,
                status = DRAFT,
                wordCount = wordCount,
                summary = summary,
                readingMinutes = computeReadingMinutes(wordCount),
                readabilityScore = editorjs.readabilityScore(doc).score,
                language = editorjs.detectLanguage(command.title, summary, doc),
                thumbnailUrl = editorjs.extractThumbnailUrl(doc),
                creationDateTime = now,
                modificationDateTime = now,
            ),
        )
    }

    private fun createContent(command: CreateStoryCommand, story: StoryEntity, now: Date): StoryContentEntity? {
        if (command.content.isNullOrEmpty()) {
            return null
        }

        return storyContentDao.save(
            StoryContentEntity(
                story = story,
                title = story.title,
                summary = story.summary,
                tagline = story.tagline,
                language = story.language,
                content = command.content,
                contentType = "application/editorjs",
                creationDateTime = now,
                modificationDateTime = now,
            ),
        )
    }

    @Transactional
    fun update(command: UpdateStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_title", command.title)
        logger.add("request_content", command.content?.take(100))
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "UpdateStoryCommand")

        val story = execute(command)

        val payload = UpdateStoryCommand(
            title = command.title,
            content = command.content,
        )
        notify(STORY_UPDATED_EVENT, story.id!!, story.userId, command.timestamp, payload)
    }

    private fun execute(command: UpdateStoryCommand): StoryEntity {
        val story = findById(command.storyId)
        val now = Date(command.timestamp)
        val doc = editorjs.fromJson(command.content)

        updateStory(command, doc, story, now, true)
        updateContent(command, story, now)
        return story
    }

    private fun updateContent(command: UpdateStoryCommand, story: StoryEntity, now: Date): StoryContentEntity? {
        val opt = storyContentDao.findByStoryAndLanguage(story, story.language)
        if (opt.isPresent) {
            val content = opt.get()
            if (command.content.isNullOrEmpty()) {
                storyContentDao.delete(content)
                return null
            } else {
                content.language = story.language
                content.title = story.title
                content.tagline = story.tagline
                content.content = command.content
                content.modificationDateTime = now
                return storyContentDao.save(content)
            }
        } else {
            val cmd = CreateStoryCommand(
                userId = story.userId,
                title = command.title,
                content = command.content,
            )
            return createContent(cmd, story, now)
        }
    }

    private fun updateStory(
        command: UpdateStoryCommand,
        doc: EJSDocument,
        story: StoryEntity,
        now: Date,
        updateContentModificationDate: Boolean,
    ): StoryEntity {
        val wordCount = editorjs.wordCount(doc)

        story.title = command.title
        story.wordCount = editorjs.wordCount(doc)
        story.readingMinutes = computeReadingMinutes(wordCount)
        story.language = editorjs.detectLanguage(story.title, story.summary, doc)
        story.thumbnailUrl = editorjs.extractThumbnailUrl(doc)
        story.readabilityScore = editorjs.readabilityScore(doc).score
        story.modificationDateTime = now
        if (updateContentModificationDate) {
            story.contentModificationDateTime = now
        }
        return storyDao.save(story)
    }

    @Transactional
    fun publish(command: PublishStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_category_id", command.categoryId)
        logger.add("request_title", command.title)
        logger.add("request_tagline", command.tagline)
        logger.add("request_scheduled", command.scheduledPublishDateTime)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "PublishStoryCommand")

        val previousStatus = findById(command.storyId).status
        val story = execute(command)
        if (command.scheduledPublishDateTime == null) {
            if (previousStatus == DRAFT) {
                notify(
                    type = STORY_PUBLISHED_EVENT,
                    storyId = command.storyId,
                    userId = story.userId,
                    timestamp = command.timestamp,
                    payload = StoryPublishedEventPayload(
                        title = command.title,
                        tagline = command.tagline,
                        access = command.access,
                        categoryId = command.categoryId,
                    ),
                )
            } else {
                notify(
                    type = STORY_UPDATED_EVENT,
                    storyId = command.storyId,
                    userId = story.userId,
                    timestamp = command.timestamp,
                    payload = StoryUpdatedEventPayload(
                        title = command.title,
                        tagline = command.tagline,
                        access = command.access,
                        categoryId = command.categoryId,
                    ),
                )
            }
        } else {
            val payload = StoryPublicationScheduledEventPayload(
                title = command.title,
                tagline = command.tagline,
                access = command.access,
                scheduledPublishDateTime = DateUtils.beginingOfTheDay(command.scheduledPublishDateTime!!),
                categoryId = command.categoryId,
            )
            notify(STORY_PUBLICATION_SCHEDULED_EVENT, command.storyId, story.userId, command.timestamp, payload)
        }
    }

    @Transactional
    fun onPublished(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyDao.findById(event.entityId.toLong()).get()

        updateSEOInformation(story)
        tagService.onStoryPublished(story)
        userService.onStoryPublished(story)
    }

    @Transactional
    fun onUpdated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyDao.findById(event.entityId.toLong()).get()

        if (story.status == PUBLISHED) {
            updateSEOInformation(story)
        }
    }

    @Transactional
    fun updateSEOInformation(story: StoryEntity) {
        extractTags(story)
        generateSummary(story)
    }

    private fun generateSummary(story: StoryEntity) {
        val content = storyContentDao.findByStoryAndLanguage(story, story.language).get()
        val summary = summaryGenerator.generate(content, SUMMARY_MAX_LEN)

        if (!summary?.content.isNullOrEmpty()) {
            // Update the content
            story.summary = summary?.content
            story.sexuallyExplicitContent = summary?.sexuallyExplicitContent ?: false
            content.modificationDateTime = Date()
            storyDao.save(story)

            // Update the summary
            content.summary = summary?.content
            content.modificationDateTime = Date()
            storyContentDao.save(content)
        }
    }

    private fun extractTags(story: StoryEntity) {
        val content = storyContentDao.findByStoryAndLanguage(story, story.language).get()
        val tags = tagExtractor.extract(content)

        // Update the content
        if (tags.isNotEmpty()) {
            story.tags = tagService.findOrCreate(tags)
            storyDao.save(story)
        }
    }

    fun execute(command: PublishStoryCommand): StoryEntity {
        val story = findById(command.storyId)
        val now = Date(command.timestamp)

        // Update story
        if (command.title != null) {
            story.title = command.title
        }
        if (command.access != null) {
            story.access = command.access!!
        }
        if (command.tagline != null) {
            story.tagline = command.tagline
        }
        if (command.categoryId != null) {
            story.categoryId = command.categoryId
        }
        if (command.productId != null) {
            story.productId = command.productId
        }

        // Change status
        if (story.status == DRAFT) {
            if (command.scheduledPublishDateTime == null) {
                story.status = PUBLISHED
                story.publishedDateTime = now
            } else {
                story.scheduledPublishDateTime = command.scheduledPublishDateTime
            }
        } else {
            story.scheduledPublishDateTime = null
        }

        // Extract information from content: Summary, Video,  Thumbnail
        val content = storyContentDao.findByStoryAndLanguage(story, story.language)
        if (content.isPresent) {
            content.get().content?.let {
                val doc = editorjs.fromJson(it)
                story.thumbnailUrl = editorjs.extractThumbnailUrl(doc)
                story.video = editorjs.detectVideo(doc)
            }
        }

        // WPP...
        story.wppScore = validateWPPEligibility(story).score
        story.modificationDateTime = now
        story.readabilityScore = computeReadabilityScore(story)
        return story
    }

    @Transactional
    fun unpublish(command: UnpublishStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "UnpublishStoryCommand")
        if (execute(command)) {
            notify(
                type = STORY_UNPUBLISHED_EVENT,
                storyId = command.storyId,
                userId = null,
                command.timestamp,
            )
        }
    }

    @Transactional
    fun onUnpublished(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyDao.findById(event.entityId.toLong()).get()

        userService.onStoryUnpublished(story)
    }

    private fun execute(command: UnpublishStoryCommand): Boolean {
        val story = findById(command.storyId)
        return if (story.status == PUBLISHED) {
            story.status = DRAFT
            story.modificationDateTime = Date(command.timestamp)
            storyDao.save(story)

            true
        } else {
            false
        }
    }

    @Transactional
    fun delete(command: DeleteStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "DeleteStoryCommand")
        try {
            val story = execute(command)
            notify(STORY_DELETED_EVENT, command.storyId, story.userId, command.timestamp)
        } catch (ex: NotFoundException) {
            logger.add("story_already_deleted", true)
        }
    }

    @Transactional
    fun onDeleted(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val story = storyDao.findById(event.entityId.toLong()).get()

        userService.onStoryDeleted(story)
        tagService.onStoryDeleted(story)
    }

    private fun execute(command: DeleteStoryCommand): StoryEntity {
        val story = findById(command.storyId)
        story.deleted = true
        story.deletedDateTime = Date(command.timestamp)
        return storyDao.save(story)
    }

    @Transactional(noRollbackFor = [ImportException::class])
    fun import(command: ImportStoryCommand): StoryEntity {
        logger.add("request_url", command.url)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "ImportStoryCommand")
        try {
            if (isAlreadyImported(command.url)) {
                throw ImportException(
                    error = Error(
                        code = ErrorCode.STORY_ALREADY_IMPORTED,
                        data = mapOf("url" to command.url),
                    ),
                )
            }

            val story = execute(command)
            notify(
                STORY_IMPORTED_EVENT,
                story.id!!,
                command.userId,
                command.timestamp,
                StoryImportedEventPayload(command.url),
            )
            return story
        } catch (ex: Exception) {
            LOGGER.error("Unable to import ${command.url}", ex)

            val payload = StoryImportFailedEventPayload(
                url = command.url,
                exceptionClass = ex.javaClass.name,
                message = getMessage(ex),
                statusCode = getStatusCode(ex),
            )
            notify(STORY_IMPORT_FAILED_EVENT, -1, command.userId, command.timestamp, payload)

            if (ex is ImportException) {
                throw ex
            } else {
                throw ImportException(
                    error = Error(
                        code = ErrorCode.STORY_IMPORT_FAILED,
                        data = mapOf("url" to command.url),
                    ),
                    ex,
                )
            }
        }
    }

    private fun execute(request: ImportStoryCommand): StoryEntity {
        val webpage = scraperService.scape(URL(request.url))

        val doc = editorjs.fromHtml(webpage.content)
        if (editorjs.toText(doc).trim().isEmpty()) {
            throw ConflictException(Error(ErrorCode.STORY_WITHOUT_CONTENT))
        }

        val now = Date(clock.millis())
        val story = createStory(request, webpage, now)
        createContent(webpage, story, now)
        return story
    }

    private fun createStory(command: ImportStoryCommand, webpage: WebPage, now: Date): StoryEntity {
        val doc = editorjs.fromHtml(webpage.content)
        val wordCount = editorjs.wordCount(doc)
        val story = StoryEntity(
            userId = command.userId,
            title = webpage.title,
            status = DRAFT,
            thumbnailUrl = webpage.image,
            creationDateTime = now,
            modificationDateTime = now,
            publishedDateTime = null,
            sourceUrl = webpage.url,
            sourceUrlHash = hash(webpage.url),
            sourceSite = webpage.siteName,
            tags = tagService.findOrCreate(webpage.tags),
            language = editorjs.detectLanguage(webpage.title, null, doc),
            wordCount = wordCount,
            readingMinutes = computeReadingMinutes(wordCount),
            summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN),
        )
        return storyDao.save(story)
    }

    private fun createContent(webpage: WebPage, story: StoryEntity, now: Date): StoryContentEntity {
        val doc = editorjs.fromHtml(webpage.content)
        return createContent(doc, story, now)
    }

    private fun computeReadingMinutes(wordCount: Int): Int =
        Math.ceil(wordCount.toDouble() / WORDS_PER_MINUTES).toInt()

    private fun computeReadabilityScore(story: StoryEntity): Int {
        try {
            val content = storyContentDao.findByStoryAndLanguage(story, story.language).getOrNull() ?: return -1
            val doc = editorjs.fromJson(content.content)
            return editorjs.readabilityScore(doc).score
        } catch (ex: Exception) {
            LOGGER.warn("Unable to compute readability score of Story#${story.id}")
            return -1
        }
    }

    private fun createContent(doc: EJSDocument, story: StoryEntity, now: Date) = storyContentDao.save(
        StoryContentEntity(
            story = story,
            content = editorjs.toJson(doc),
            contentType = "application/editorjs",
            language = story.language,
            creationDateTime = now,
            modificationDateTime = now,
        ),
    )

    fun notify(
        type: String,
        storyId: Long,
        userId: Long?,
        timestamp: Long,
        payload: Any? = null,
    ) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.STORY,
                type = type,
                entityId = storyId.toString(),
                userId = userId?.toString(),
                timestamp = Date(timestamp),
                payload = payload,
            ),
        )
        logger.add("evt_id", eventId)

        val eventPayload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, eventPayload)
        eventStream.publish(type, eventPayload)
    }

    fun search(request: SearchStoryRequest, deviceId: String? = null): List<StoryEntity> {
        logger.add("request_language", request.language)
        logger.add("request_story_ids", request.storyIds)
        logger.add("request_category_ids", request.categoryIds)
        logger.add("request_published_end_date", request.publishedEndDate)
        logger.add("request_published_start_date", request.publishedStartDate)
        logger.add("request_status", request.status)
        logger.add("request_user_ids", request.userIds)
        logger.add("request_sort_by", request.sortBy)
        logger.add("request_sort_order", request.sortOrder)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_bubble_down_viewed_stories", request.bubbleDownViewedStories)
        logger.add("request_exclude_stories_from_subscription", request.excludeStoriesFromSubscriptions)
        logger.add("request_search_context_user_id", request.searchContext?.userId)

        val stories = searchStories(request)
        logger.add("count", stories.size)

        return stories
    }

    fun readability(id: Long): ReadabilityResult {
        val story = findById(id)
        val content = storyContentDao.findByStoryAndLanguage(story, story.language)
        val json = if (content.isPresent) content.get().content else null
        val doc = editorjs.fromJson(json)
        return editorjs.readabilityScore(doc)
    }

    fun validateWPPEligibility(id: Long): WPPValidation {
        val story = findById(id)
        return validateWPPEligibility(story)
    }

    private fun validateWPPEligibility(story: StoryEntity): WPPValidation =
        wppService.validate(story)

    private fun isAlreadyImported(url: String): Boolean {
        val hash = hash(url)
        val stories = storyDao.findBySourceUrlHash(hash)
        return stories.isNotEmpty()
    }

    private fun hash(url: String): String {
        val normalized = if (url.endsWith("/")) url.substring(0, url.length - 1) else url
        return DigestUtils.md5Hex(normalized.lowercase())
    }

    fun searchStories(request: SearchStoryRequest): List<StoryEntity> {
        val xrequest = excludeSubscribedBlog(request)

        val builder = SearchStoryQueryBuilder(tagService)
        val sql = builder.query(xrequest)
        val params = builder.parameters(xrequest)
        val query = em.createNativeQuery(sql, StoryEntity::class.java)
        Predicates.setParameters(query, params)
        val stories = query.resultList as List<StoryEntity>

        return storySearchFilter.filter(xrequest, stories).take(request.limit)
    }

    private fun excludeSubscribedBlog(request: SearchStoryRequest): SearchStoryRequest {
        if (request.excludeStoriesFromSubscriptions && request.searchContext?.userId != null) {
            val userIds = subscriptionDao.findBySubscriberId(request.searchContext!!.userId!!).map { it.userId }
            if (userIds.isNotEmpty()) {
                val excludeUserIds = mutableSetOf<Long>()
                excludeUserIds.addAll(userIds)
                excludeUserIds.addAll(request.excludeUserIds)
                return request.copy(excludeUserIds = excludeUserIds.toList())
            }
        }
        return request
    }

    fun url(story: StoryEntity, language: String? = null): String =
        websiteUrl + mapper.slug(story, language)

    private fun getStatusCode(ex: Exception): Int? =
        if (ex is HttpClientErrorException) {
            ex.statusCode.value()
        } else if (ex is HttpStatusException) {
            ex.statusCode
        } else {
            null
        }

    private fun getMessage(ex: Exception): String? =
        if (ex is WutsiException) {
            ex.error.code
        } else {
            ex.message
        }
}
