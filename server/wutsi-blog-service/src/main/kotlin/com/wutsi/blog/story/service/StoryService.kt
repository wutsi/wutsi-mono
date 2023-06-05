package com.wutsi.blog.story.service

import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.ReadabilityDto
import com.wutsi.blog.client.story.ReadabilityRuleDto
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_CREATED_EVENT
import com.wutsi.blog.event.EventType.STORY_DELETED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORT_FAILED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLICATION_SCHEDULED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.EventType.STORY_UPDATED_EVENT
import com.wutsi.blog.event.StreamId
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
import com.wutsi.blog.story.dto.StoryCreatedEventPayload
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryPublicationScheduledEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StoryStatus.DRAFT
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.story.dto.WebPage
import com.wutsi.blog.story.exception.ImportException
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.util.Predicates
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.error.exception.WutsiException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
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
import javax.persistence.EntityManager
import kotlin.jvm.optionals.getOrNull

@Service
class StoryService(
    private val clock: Clock,
    private val storyDao: StoryRepository,
    private val storyContentDao: StoryContentRepository,
    private val auth: AuthenticationService,
    private val editorjs: EditorJSService,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val tags: TagService,
    private val mapper: StoryMapper,
    private val viewService: ViewService,
    private val tagService: TagService,
    private val scaperService: WebScaperService,
    private val eventStream: EventStream,
    private val eventStore: EventStore,

    @Value("\${wutsi.readability.score-threshold}") private val scoreThreshold: Int,
    @Value("\${wutsi.website.url}") private val websiteUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryService::class.java)
        private const val WORDS_PER_MINUTES = 250
        const val SUMMARY_MAX_LEN = 200
    }

    fun findById(id: Long): StoryEntity {
        val story = storyDao.findById(id)
            .orElseThrow { NotFoundException(Error("story_not_found")) }

        if (story.deleted) {
            throw NotFoundException(Error("story_not_found"))
        }
        return story
    }

    fun findContent(story: StoryEntity, language: String?): Optional<StoryContentEntity> =
        storyContentDao.findByStoryAndLanguage(story, language)

    @Transactional
    fun create(command: CreateStoryCommand): StoryEntity {
        logger.add("request_user_id", command.userId)
        logger.add("request_title", command.title)
        logger.add("request_content", command.content?.take(100))
        logger.add("request_timestamp", command.timestamp)

        val story = execute(command)

        val payload = StoryCreatedEventPayload(
            title = command.title,
            content = command.content,
        )
        notify(STORY_CREATED_EVENT, story.id!!, command.userId, command.timestamp, payload)
        return story
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
        return save(
            StoryEntity(
                userId = command.userId!!,
                title = command.title,
                status = StoryStatus.DRAFT,
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

        return save(
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

        updateStory(command, doc, story, now)
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
                content.summary = story.summary
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

    private fun updateStory(command: UpdateStoryCommand, doc: EJSDocument, story: StoryEntity, now: Date): StoryEntity {
        val wordCount = editorjs.wordCount(doc)
        val summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN)

        story.title = command.title
        story.modificationDateTime = now
        story.summary = summary
        story.wordCount = editorjs.wordCount(doc)
        story.readingMinutes = computeReadingMinutes(wordCount)
        story.language = editorjs.detectLanguage(story.title, story.summary, doc)
        story.thumbnailUrl = editorjs.extractThumbnailUrl(doc)
        story.readabilityScore = editorjs.readabilityScore(doc).score
        return save(story)
    }

    @Transactional
    fun publish(command: PublishStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_title", command.title)
        logger.add("request_tagline", command.tagline)
        logger.add("request_summary", command.summary)
        logger.add("request_tags", command.tags)
        logger.add("request_scheduled", command.scheduledPublishDateTime)
        logger.add("request_timestamp", command.timestamp)

        val story = execute(command)
        if (command.scheduledPublishDateTime == null) {
            val payload = StoryPublishedEventPayload(
                title = command.title,
                summary = command.summary,
                topicId = command.topicId,
                tags = command.tags,
                tagline = command.tagline,
                access = command.access,
            )
            notify(STORY_PUBLISHED_EVENT, command.storyId, story.userId, command.timestamp, payload)
        } else {
            val payload = StoryPublicationScheduledEventPayload(
                title = command.title,
                summary = command.summary,
                topicId = command.topicId,
                tags = command.tags,
                tagline = command.tagline,
                access = command.access,
                scheduledPublishDateTime = command.scheduledPublishDateTime!!,
            )
            notify(STORY_PUBLICATION_SCHEDULED_EVENT, command.storyId, story.userId, command.timestamp, payload)
        }
    }

    fun execute(command: PublishStoryCommand): StoryEntity {
        val story = findById(command.storyId)
        val now = Date(command.timestamp)

        // Update story
        if (command.title != null) {
            story.title = command.title
        }
        if (command.summary != null) {
            story.summary = command.summary
        }
        if (command.topicId != null) {
            story.topicId = command.topicId
        }
        if (command.access != null) {
            story.access = command.access!!
        }
        if (command.tagline != null) {
            story.tagline = command.tagline
        }
        if (command.tags != null) {
            story.tags = tagService.find(command.tags!!)
        }

        // Change status
        if (story.status == DRAFT) {
            if (command.scheduledPublishDateTime == null) {
                story.status = StoryStatus.PUBLISHED
                story.publishedDateTime = now
            } else {
                story.scheduledPublishDateTime = command.scheduledPublishDateTime
            }
        } else {
            story.scheduledPublishDateTime = null
        }
        story.modificationDateTime = now
        story.readabilityScore = computeReadabilityScore(story)
        return story
    }

    @Transactional
    fun delete(command: DeleteStoryCommand) {
        logger.add("request_story_id", command.storyId)
        logger.add("request_timestamp", command.timestamp)
        try {
            execute(command)
            notify(STORY_DELETED_EVENT, command.storyId, null, command.timestamp)
        } catch (ex: NotFoundException) {
            logger.add("story_already_deleted", true)
        }
    }

    private fun execute(command: DeleteStoryCommand) {
        val story = findById(command.storyId)
        story.deleted = true
        story.deletedDateTime = Date(command.timestamp)
        storyDao.save(story)
    }

    @Transactional(noRollbackFor = [ImportException::class])
    fun import(command: ImportStoryCommand): StoryEntity {
        logger.add("url", command.url)
        logger.add("user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        try {
            if (isAlreadyImported(command.url)) {
                throw ImportException(
                    error = Error(
                        code = "story_already_imported",
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
                        code = "import_failed",
                        data = mapOf("url" to command.url),
                    ),
                    ex,
                )
            }
        }
    }

    private fun execute(request: ImportStoryCommand): StoryEntity {
        val webpage = scaperService.scape(URL(request.url))

        val doc = editorjs.fromHtml(webpage.content)
        if (editorjs.toText(doc).trim().isEmpty()) {
            throw ConflictException(Error("no_content"))
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
            status = StoryStatus.DRAFT,
            thumbnailUrl = webpage.image,
            creationDateTime = now,
            modificationDateTime = now,
            publishedDateTime = null,
            sourceUrl = webpage.url,
            sourceUrlHash = hash(webpage.url),
            sourceSite = webpage.siteName,
            tags = tags.find(webpage.tags),
            language = editorjs.detectLanguage(webpage.title, null, doc),
            wordCount = wordCount,
            readingMinutes = computeReadingMinutes(wordCount),
            summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN),
        )
        return save(story)
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

    private fun createContent(doc: EJSDocument, story: StoryEntity, now: Date) = save(
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

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }

    fun search(request: SearchStoryRequest, deviceId: String? = null): List<StoryEntity> {
        logger.add("request_language", request.language)
        logger.add("request_story_ids", request.storyIds)
        logger.add("request_published_end_date", request.publishedEndDate)
        logger.add("request_published_start_date", request.publishedStartDate)
        logger.add("request_status", request.status)
        logger.add("request_user_ids", request.userIds)
        logger.add("request_sort_by", request.sortBy)
        logger.add("request_sort_order", request.sortOrder)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_context_device_id", request.context.deviceId)
        logger.add("request_context_user_id", request.context.userId)

        val stories = searchStories(request)
        logger.add("count", stories.size)

        return stories
    }

    fun count(request: SearchStoryRequest): CountStoryResponse {
        val count = countStories(request)
        logger.add("StoryCount", count)

        return CountStoryResponse(
            total = count.toInt(),
        )
    }

    fun readability(id: Long): GetStoryReadabilityResponse {
        val story = findById(id)
        val content = storyContentDao.findByStoryAndLanguage(story, story.language)
        val json = if (content.isPresent) content.get().content else null
        val doc = editorjs.fromJson(json)
        val result = editorjs.readabilityScore(doc)
        return GetStoryReadabilityResponse(
            readability = ReadabilityDto(
                score = result.score,
                scoreThreshold = scoreThreshold,
                rules = result.ruleResults.map {
                    ReadabilityRuleDto(
                        name = it.rule.name(),
                        score = it.score,
                    )
                },
            ),
        )
    }

    fun isAlreadyImported(url: String): Boolean {
        val hash = hash(url)
        val stories = storyDao.findBySourceUrlHash(hash)
        return stories.isNotEmpty()
    }

    private fun hash(url: String): String {
        val normalized = if (url.endsWith("/")) url.substring(0, url.length - 1) else url
        return DigestUtils.md5Hex(normalized.lowercase())
    }

    fun searchStories(request: SearchStoryRequest): List<StoryEntity> {
        val builder = SearchStoryQueryBuilder(tagService)
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, StoryEntity::class.java)
        Predicates.setParameters(query, params)
        var stories = query.resultList as List<StoryEntity>

        // Bubble down viewed stories
        if (stories.isNotEmpty() && !request.context.deviceId.isNullOrEmpty()) {
            stories = bubbleDownViewedStories(request, stories)
        }

        // Dedup
        if (request.dedupUser) {
            stories = dedupUser(stories)
        }

        return stories.take(request.limit)
    }

    private fun bubbleDownViewedStories(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        val storyIds = stories.map { it.id!! }
        val viewed = storyIds.filter { viewService.contains(request.context.deviceId, it) }
        if (viewed.isEmpty()) {
            return stories
        }

        val sortedIds = mutableListOf<Long>()
        sortedIds.addAll(storyIds.filter { !viewed.contains(it) })
        sortedIds.addAll(viewed)

        val storyMap = stories.associateBy { it.id }
        return sortedIds.map { storyMap[it]!! }
    }

    private fun dedupUser(stories: List<StoryEntity>): List<StoryEntity> {
        val authorIds = mutableSetOf<Long>()
        return stories.filter {
            authorIds.add(it.userId)
        }
    }

    fun url(story: StoryEntity, language: String? = null): String =
        websiteUrl + mapper.slug(story, language)

    private fun save(story: StoryEntity): StoryEntity {
        val result = storyDao.save(story)
        return result
    }

    private fun save(content: StoryContentEntity): StoryContentEntity =
        storyContentDao.save(content)

    fun countStories(request: SearchStoryRequest): Number {
        val builder = SearchStoryQueryBuilder(tagService)
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)
        return query.singleResult as Number
    }

    private fun findUser(accessToken: String): UserEntity {
        try {
            val session = auth.findByAccessToken(accessToken)
            return if (session.runAsUser != null) session.runAsUser!! else session.account.user
        } catch (ex: NotFoundException) {
            throw ForbiddenException(Error("session_not_found"))
        }
    }

    private fun findStory(id: Long, accessToken: String): StoryEntity {
        val user = findUser(accessToken)
        val story = findById(id)
        if (user.id != story.userId && !user.superUser) {
            throw ForbiddenException(Error("permission_denied"))
        }
        return story
    }

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
