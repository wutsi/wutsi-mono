package com.wutsi.blog.story.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.client.extractor.WebPageDto
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.ReadabilityDto
import com.wutsi.blog.client.story.ReadabilityRuleDto
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StoryStatus.draft
import com.wutsi.blog.client.story.StoryStatus.published
import com.wutsi.blog.story.dao.SearchStoryQueryBuilder
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.domain.StoryContent
import com.wutsi.blog.story.domain.Topic
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.util.Predicates
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Value("\${wutsi.readability.score-threshold}") private val scoreThreshold: Int,
    @Value("\${wutsi.website.url}") private val websiteUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryService::class.java)
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
    fun publish(story: Story, request: PublishStoryRequest): Story {
        try {
            return publishStory(story, request, null)
        } finally {
            log(story)
        }
    }

    fun publishStory(story: Story, request: PublishStoryRequest, publishDateTime: Date? = null): Story {
        if (story.status == draft) {
            publishDraft(story, request, publishDateTime)
        } else {
            publishPublished(story, request)
        }
        return story
    }

    @Transactional
    fun publishScheduled(story: Story): Story {
        if (story.status == draft && story.scheduledPublishDateTime != null) {
            updatePublishStatus(story, Date())
            save(story)
        }
        return story
    }

    @Transactional
    fun delete(id: Long) {
        val story = findById(id)

        story.deleted = true
        story.deletedDateTime = Date()
        storyDao.save(story)
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

    private fun checkIfAlreadyImported(url: String) {
        if (isAlreadyImported(url)) {
            throw ConflictException(Error("story_already_imported"))
        }
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

    private fun createStory(request: ImportStoryRequest, doc: EJSDocument, page: WebPageDto, now: Date): Story {
        val summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN)
        val story = Story(
            userId = findUser(request.accessToken!!).id!!,
            title = page.title,
            status = StoryStatus.draft,
            wordCount = editorjs.wordCount(doc),
            summary = summary,
            readingMinutes = editorjs.readingMinutes(doc),
            language = editorjs.detectLanguage(page.title, summary, doc, request.siteId!!),
            thumbnailUrl = editorjs.extractThumbnailUrl(doc),
            creationDateTime = now,
            modificationDateTime = now,
            publishedDateTime = null,
            sourceUrl = page.url,
            sourceUrlHash = hash(page.url),
            sourceSite = page.siteName,
            tags = tags.find(page.tags),
        )
        return save(story)
    }

    private fun createStory(request: SaveStoryRequest, doc: EJSDocument, now: Date): Story {
        val summary = editorjs.extractSummary(doc, SUMMARY_MAX_LEN)

        return save(
            Story(
                userId = findUser(request.accessToken!!).id!!,
                title = request.title,
                status = StoryStatus.draft,
                wordCount = editorjs.wordCount(doc),
                summary = summary,
                readingMinutes = editorjs.readingMinutes(doc),
                readabilityScore = editorjs.readabilityScore(doc).score,
                language = editorjs.detectLanguage(request.title, summary, doc, request.siteId!!),
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

        story.title = request.title
        story.modificationDateTime = now
        story.wordCount = editorjs.wordCount(doc)
        story.readingMinutes = editorjs.readingMinutes(doc)
        story.language = editorjs.detectLanguage(request.title, summary, doc, story.siteId)
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

    private fun publishDraft(story: Story, request: PublishStoryRequest, publishDateTime: Date?): Story {
        val now = Date(clock.millis())
        update(story, now, request)

        if (request.scheduledPublishDateTime == null) {
            updatePublishStatus(story, publishDateTime ?: now)
        } else {
            story.scheduledPublishDateTime = request.scheduledPublishDateTime
        }
        return save(story)
    }

    private fun publishPublished(story: Story, request: PublishStoryRequest): Story {
        val now = Date(clock.millis())
        update(story, now, request)
        return save(story)
    }

    private fun update(story: Story, now: Date, request: PublishStoryRequest) {
        story.title = request.title
        story.summary = request.summary
        story.tagline = request.tagline
        story.tags = tags.findOrCreate(request.tags)
        story.topicId = request.topidId
        story.modificationDateTime = now
        story.publishToSocialMedia = request.publishToSocialMedia
        story.socialMediaMessage = if (request.publishToSocialMedia == true) request.socialMediaMessage else null
        story.access = request.access
    }

    private fun updatePublishStatus(story: Story, now: Date) {
        story.status = published
        story.publishedDateTime = now
        story.live = true
        story.liveDateTime = now
    }

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
}
