package com.wutsi.blog.story.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.ReadabilityDto
import com.wutsi.blog.client.story.ReadabilityRuleDto
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.STORY_IMPORT_FAILED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLICATION_SCHEDULED_EVENT
import com.wutsi.blog.event.EventType.STORY_PUBLISHED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.SearchStoryQueryBuilder
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TopicRepository
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.domain.StoryContent
import com.wutsi.blog.story.domain.Topic
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryPublicationScheduledEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StoryStatus.DRAFT
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
    private val topicDao: TopicRepository,
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

    fun findById(id: Long): Story {
        val story = storyDao.findById(id)
            .orElseThrow { NotFoundException(Error("story_not_found")) }

        if (story.deleted) {
            throw NotFoundException(Error("story_not_found"))
        }
        return story
    }

    fun findContent(story: Story, language: String?): Optional<StoryContent> =
        storyContentDao.findByStoryAndLanguage(story, language)

    @Transactional
    fun create(request: SaveStoryRequest): SaveStoryResponse {
        try {
            val story = createStory(request)
            log(story)
            return SaveStoryResponse(
                storyId = story.id!!,
            )
        } catch (ex: JsonProcessingException) {
            LOGGER.error(request.content)
            throw ex
        }
    }

    fun createStory(request: SaveStoryRequest): Story {
        val now = Date(clock.millis())
        val doc = editorjs.fromJson(request.content)
        val story = createStory(request, doc, now)
        createContent(request, story, now)
        return story
    }

    @Transactional
    fun update(id: Long, request: SaveStoryRequest): SaveStoryResponse {
        val story = findStory(id, request.accessToken!!)
        try {
            val response = update(story, request)

            log(story)
            return response
        } catch (ex: JsonProcessingException) {
            LOGGER.error(request.content)
            throw ex
        } finally {
            log(story)
        }
    }

    @Transactional
    fun publish(command: PublishStoryCommand) {
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

    fun execute(command: PublishStoryCommand): Story {
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

//    @Transactional
//    fun publishScheduled(story: Story): Story {
//        if (story.status == DRAFT && story.scheduledPublishDateTime != null) {
//            updatePublishStatus(story, Date())
//            save(story)
//        }
//        return story
//    }

    @Transactional
    fun delete(id: Long) {
        val story = findById(id)

        story.deleted = true
        story.deletedDateTime = Date()
        storyDao.save(story)
    }

    @Transactional(noRollbackFor = [ImportException::class])
    fun import(command: ImportStoryCommand): Story {
        logger.add("url", command.url)
        logger.add("user_id", command.userId)

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

    private fun execute(request: ImportStoryCommand): Story {
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

    private fun createStory(command: ImportStoryCommand, webpage: WebPage, now: Date): Story {
        val doc = editorjs.fromHtml(webpage.content)
        val wordCount = editorjs.wordCount(doc)
        val story = Story(
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

    private fun createContent(webpage: WebPage, story: Story, now: Date): StoryContent {
        val doc = editorjs.fromHtml(webpage.content)
        return createContent(doc, story, now)
    }

    private fun computeReadingMinutes(wordCount: Int): Int =
        Math.ceil(wordCount.toDouble() / WORDS_PER_MINUTES).toInt()

    private fun computeReadabilityScore(story: Story): Int {
        val content = storyContentDao.findByStory(story).find { it.language == story.language } ?: return -1
        val doc = editorjs.fromJson(content.content)
        return editorjs.readabilityScore(doc).score
    }

    private fun createContent(doc: EJSDocument, story: Story, now: Date) = save(
        StoryContent(
            story = story,
            content = editorjs.toJson(doc),
            contentType = "application/editorjs",
            language = story.language,
            creationDateTime = now,
            modificationDateTime = now,
        ),
    )

    @Transactional
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
                userId = userId.toString(),
                timestamp = Date(timestamp),
                payload = payload,
            ),
        )
        logger.add("evt_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }

    fun search(request: SearchStoryRequest, deviceId: String? = null): SearchStoryResponse {
        log(request)

        val stories = searchStories(request)
        logger.add("StoryCount", stories.size)

        return SearchStoryResponse(
            stories = stories.map { mapper.toStorySummaryDto(it) },
        )
    }

    fun count(request: SearchStoryRequest): CountStoryResponse {
        log(request)

        val count = countStories(request)
        logger.add("StoryCount", count)

        return CountStoryResponse(
            total = count.toInt(),
        )
    }

    private fun toStoryDto(story: Story, content: Optional<StoryContent>, topic: Topic?): StoryDto =
        mapper.toStoryDto(story, content, topic)

    private fun log(request: SearchStoryRequest) {
        logger.add("Language", request.language)
        logger.add("StoryIds", request.storyIds)
        logger.add("PublishedEndDate", request.publishedEndDate)
        logger.add("PublishedStartDate", request.publishedStartDate)
        logger.add("Status", request.status)
        logger.add("UserIds", request.userIds)
        logger.add("Live", request.live)
        logger.add("SortBy", request.sortBy)
        logger.add("SortOrder", request.sortOrder)
        logger.add("Limit", request.limit)
        logger.add("Offset", request.offset)
        logger.add("ContextDeviceId", request.context.deviceId)
        logger.add("ContextUserId", request.context.userId)
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

    fun searchStories(request: SearchStoryRequest): List<Story> {
        val builder = SearchStoryQueryBuilder(tagService)
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, Story::class.java)
        Predicates.setParameters(query, params)
        var stories = query.resultList as List<Story>

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

    private fun bubbleDownViewedStories(request: SearchStoryRequest, stories: List<Story>): List<Story> {
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

    private fun dedupUser(stories: List<Story>): List<Story> {
        val authorIds = mutableSetOf<Long>()
        return stories.filter {
            authorIds.add(it.userId)
        }
    }

    fun url(story: Story, language: String? = null): String =
        websiteUrl + mapper.slug(story, language)

    private fun save(story: Story): Story {
        val result = storyDao.save(story)
        return result
    }

    private fun save(content: StoryContent): StoryContent =
        storyContentDao.save(content)

    fun countStories(request: SearchStoryRequest): Number {
        val builder = SearchStoryQueryBuilder(tagService)
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)
        return query.singleResult as Number
    }

    private fun createStory(request: SaveStoryRequest, doc: EJSDocument, now: Date): Story {
        val summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN)
        val wordCount = editorjs.wordCount(doc)
        return save(
            Story(
                userId = findUser(request.accessToken!!).id!!,
                title = request.title,
                status = StoryStatus.DRAFT,
                wordCount = wordCount,
                summary = summary,
                readingMinutes = computeReadingMinutes(wordCount),
                readabilityScore = editorjs.readabilityScore(doc).score,
                language = editorjs.detectLanguage(request.title, summary, doc),
                thumbnailUrl = editorjs.extractThumbnailUrl(doc),
                creationDateTime = now,
                modificationDateTime = now,
                siteId = request.siteId!!,
                sourceUrl = request.sourceUrl,
                sourceSite = request.sourceSite,
                sourceUrlHash = request.sourceUrl?.let { hash(it) },
            ),
        )
    }

    private fun createContent(request: SaveStoryRequest, story: Story, now: Date) = save(
        StoryContent(
            story = story,
            title = story.title,
            summary = story.summary,
            tagline = story.tagline,
            language = story.language,
            content = request.content,
            contentType = request.contentType,
            creationDateTime = now,
            modificationDateTime = now,
        ),
    )

    private fun update(story: Story, request: SaveStoryRequest): SaveStoryResponse {
        val now = Date(clock.millis())
        val doc = editorjs.fromJson(request.content)

        updateStory(request, doc, story, now)
        updateContent(request, story, now)

        return SaveStoryResponse(
            storyId = story.id!!,
        )
    }

    private fun updateContent(request: SaveStoryRequest, story: Story, now: Date): StoryContent {
        val opt = storyContentDao.findByStoryAndLanguage(story, story.language)
        if (opt.isPresent) {
            val content = opt.get()
            content.language = story.language
            content.title = story.title
            content.tagline = story.tagline
            content.summary = story.summary
            content.content = request.content
            content.contentType = request.contentType
            content.modificationDateTime = now
            return storyContentDao.save(content)
        } else {
            return createContent(request, story, now)
        }
    }

    private fun updateStory(request: SaveStoryRequest, doc: EJSDocument, story: Story, now: Date): Story {
        val summary = if (story.summary == null || story.summary.isNullOrBlank()) {
            editorjs.extractSummary(doc, SUMMARY_MAX_LEN)
        } else {
            story.summary
        }

        val wordCount = editorjs.wordCount(doc)

        story.title = request.title
        story.modificationDateTime = now
        story.wordCount = editorjs.wordCount(doc)
        story.readingMinutes = computeReadingMinutes(wordCount)
        story.language = editorjs.detectLanguage(request.title, summary, doc)
        story.thumbnailUrl = editorjs.extractThumbnailUrl(doc)
        story.readabilityScore = editorjs.readabilityScore(doc).score
        story.summary = summary
        return save(story)
    }

    private fun findUser(accessToken: String): UserEntity {
        try {
            val session = auth.findByAccessToken(accessToken)
            return if (session.runAsUser != null) session.runAsUser!! else session.account.user
        } catch (ex: NotFoundException) {
            throw ForbiddenException(Error("session_not_found"))
        }
    }

    private fun findStory(id: Long, accessToken: String): Story {
        val user = findUser(accessToken)
        val story = findById(id)
        if (user.id != story.userId && !user.superUser) {
            throw ForbiddenException(Error("permission_denied"))
        }
        return story
    }

//    private fun update(story: Story, now: Date, request: PublishStoryRequest) {
//        story.title = request.title
//        story.summary = request.summary
//        story.tagline = request.tagline
//        story.tags = tags.findOrCreate(request.tags)
//        story.topicId = request.topidId
//        story.modificationDateTime = now
//        story.publishToSocialMedia = request.publishToSocialMedia
//        story.socialMediaMessage = if (request.publishToSocialMedia == true) request.socialMediaMessage else null
//        story.access = request.access
//    }

//    private fun updatePublishStatus(story: Story, now: Date) {
//        story.status = PUBLISHED
//        story.publishedDateTime = now
//        story.live = true
//        story.liveDateTime = now
//    }

    private fun log(story: Story, logger: KVLogger = this.logger) {
        logger.add("StoryId", story.id)
        logger.add("StoryStatus", story.status)
        logger.add("StoryUser", story.userId)
        logger.add("StoryTitle", story.title)
        logger.add("StoryWordCount", story.wordCount)
        logger.add("StoryReadabilityScore", story.readabilityScore)
        logger.add("StoryReadingMinutes", story.readingMinutes)
        logger.add("WPPStatus", story.wppStatus)
        logger.add("WPPRejectionReason", story.wppRejectionReason)
    }

    private fun log(story: StoryDto, language: String? = null) {
        logger.add("StoryId", story.id)
        logger.add("StoryStatus", story.status)
        logger.add("StoryUser", story.userId)
        logger.add("StoryTitle", story.title)
        logger.add("StoryWordCount", story.wordCount)
        logger.add("StoryLanguage", language?.let { it } ?: story.language)
        logger.add("StoryReadabilityScore", story.readabilityScore)
        logger.add("StoryReadingMinutes", story.readingMinutes)
        logger.add("WPPStatus", story.wppStatus)
        logger.add("WPPRejectionReason", story.wppRejectionReason)
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
