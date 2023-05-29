package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.CommentBackend
import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.mapper.StoryMapper
import com.wutsi.blog.app.model.StoryForm
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.editor.model.PublishForm
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryContext
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.comment.dto.CommentCounter
import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.like.dto.CountLikeRequest
import com.wutsi.blog.like.dto.LikeCounter
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.tracing.TracingContext
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.text.SimpleDateFormat

@Service
class StoryService(
    private val requestContext: RequestContext,
    private val mapper: StoryMapper,
    private val ejsJsonReader: EJSJsonReader,
    private val ejsHtmlWriter: EJSHtmlWriter,
    private val ejsFilters: EJSFilterSet,
    private val userService: UserService,
    private val storyBackend: StoryBackend,
    private val likeBackend: LikeBackend,
    private val pinBackend: PinBackend,
    private val commentBackend: CommentBackend,
    private val tracingContext: TracingContext,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryService::class.java)
    }

    fun save(editor: StoryForm): StoryForm {
        var response = SaveStoryResponse()
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)) {
            response = storyBackend.create(request)
        } else if (shouldUpdate(editor)) {
            response = storyBackend.update(editor.id!!, request)
        }

        return StoryForm(
            id = response.storyId,
            title = editor.title,
            content = editor.content,
        )
    }

    fun get(id: Long): StoryModel {
        val story = storyBackend.get(id).story
        val user = userService.get(story.userId)

        val storyIds = listOf(id)
        val likes = getLikes(storyIds)
        val comments = getComments(storyIds)
        return mapper.toStoryModel(story, user, likes, comments)
    }

    fun search(
        request: SearchStoryRequest,
        pinnedStoryId: Long? = null,
        bubbleDownIds: List<Long> = emptyList(),
    ): List<StoryModel> {
        val stories = bubbleDown(storyBackend.search(request).stories, bubbleDownIds)
        if (stories.isEmpty()) {
            return emptyList()
        }

        val users = searchUserMap(stories)

        val storyIds = stories.map { it.id }
        val likes = getLikes(storyIds)
        val comments = getComments(storyIds)

        return stories.map {
            mapper.toStoryModel(it, users[it.userId], pinnedStoryId, likes, comments)
        }
    }

    fun recommend(storyId: Long, limit: Int = 20): List<StoryModel> {
        val stories = storyBackend.recommend(
            RecommendStoryRequest(
                storyId = storyId,
                limit = limit,
                context = createSearchContext(),
            ),
        ).stories.filter { it.id != storyId }

        val users = searchUserMap(stories)

        val storyIds = stories.map { it.id }
        val likes = getLikes(storyIds)
        val comments = getComments(storyIds)

        return stories.map {
            mapper.toStoryModel(it, users[it.userId], null, likes, comments)
        }
    }

    fun generateHtmlContent(story: StoryModel, summary: Boolean = false): String {
        if (story.content == null) {
            return ""
        }

        val ejs = ejsJsonReader.read(story.content, summary)
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)

        val doc = Jsoup.parse(html.toString())
        ejsFilters.filter(doc)
        return doc.html()
    }

    fun publish(editor: PublishForm) {
        storyBackend.publish(
            editor.id,
            PublishStoryRequest(
                title = editor.title,
                tagline = editor.tagline,
                summary = editor.summary,
                topidId = editor.topicId.toLong(),
                tags = editor.tags,
                scheduledPublishDateTime = if (editor.publishNow) null else SimpleDateFormat("yyyy-MM-dd").parse(editor.scheduledPublishDate),
                access = editor.access,
            ),
        )
    }

    fun count(status: StoryStatus? = null): Int {
        val userId = requestContext.currentUser()?.id
        val request = SearchStoryRequest(
            userIds = if (userId == null) emptyList() else listOf(userId),
            status = status,
            limit = Int.MAX_VALUE,
        )
        return storyBackend.count(request).total
    }

    fun import(url: String): Long {
        val request = ImportStoryRequest(
            url = url,
            accessToken = requestContext.accessToken(),
            siteId = requestContext.siteId(),
        )
        return storyBackend.import(request).storyId
    }

    fun readability(id: Long): ReadabilityModel {
        val result = storyBackend.readability(id).readability
        return mapper.toReadabilityModel(result)
    }

    fun delete(id: Long) {
        storyBackend.delete(id)
    }

    fun like(storyId: Long) {
        likeBackend.execute(
            LikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun unlike(storyId: Long) {
        likeBackend.execute(
            UnlikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun pin(storyId: Long) {
        pinBackend.execute(
            PinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun unpin(storyId: Long) {
        pinBackend.execute(
            UnpinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun getPinnedStoryId(userId: Long): Long? =
        try {
            val pins = pinBackend.search(
                SearchPinRequest(
                    userIds = listOf(userId),
                ),
            ).pins

            if (pins.isEmpty()) null else pins[0].storyId
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve pinned story of User#$userId", ex)
            null
        }

    private fun getLikes(storyIds: List<Long>): List<LikeCounter> =
        try {
            likeBackend.count(
                CountLikeRequest(
                    storyIds = storyIds,
                    deviceId = tracingContext.deviceId(),
                    userId = requestContext.currentUser()?.id,
                ),
            ).counters
        } catch (ex: Exception) {
            LOGGER.warn("Unable to search likes for $storyIds", ex)
            emptyList()
        }

    private fun getComments(storyIds: List<Long>): List<CommentCounter> =
        try {
            commentBackend.count(
                CountCommentRequest(
                    storyIds = storyIds,
                    userId = requestContext.currentUser()?.id,
                ),
            ).commentStories
        } catch (ex: Exception) {
            LOGGER.warn("Unable to search comments for $storyIds", ex)
            emptyList()
        }

    private fun bubbleDown(stories: List<StorySummaryDto>, bubbleDownIds: List<Long>): List<StorySummaryDto> =
        if (stories.isNotEmpty() && bubbleDownIds.isNotEmpty()) {
            val result = mutableListOf<StorySummaryDto>()
            val head = stories.filter { !bubbleDownIds.contains(it.id) }
            val tail = stories.filter { bubbleDownIds.contains(it.id) }
            result.addAll(head)
            result.addAll(tail)

            result
        } else {
            stories
        }

    private fun shouldUpdate(editor: StoryForm) = editor.id != null && editor.id > 0L

    private fun shouldCreate(editor: StoryForm) = (editor.id == null || editor.id == 0L) && !isEmpty(editor)

    private fun isEmpty(editor: StoryForm): Boolean {
        if (editor.title.trim().isNotEmpty()) {
            return false
        }

        val doc = ejsJsonReader.read(editor.content)
        val html = StringWriter()
        ejsHtmlWriter.write(doc, html)
        return Jsoup.parse(html.toString()).body().text().trim().isEmpty()
    }

    private fun toSaveStoryRequest(editor: StoryForm) = SaveStoryRequest(
        contentType = "application/editorjs",
        content = editor.content,
        title = editor.title,
        accessToken = requestContext.accessToken(),
        siteId = requestContext.siteId(),
    )

    private fun searchUserMap(stories: List<StorySummaryDto>): Map<Long, UserModel?> {
        val userIds = stories.map { it.userId }.toSet().toList()
        if (userIds.isEmpty()) {
            return emptyMap()
        } else if (userIds.size == 1) {
            val user = userService.get(userIds[0])
            return mapOf(user.id to user)
        } else {
            return userService.search(
                SearchUserRequest(
                    userIds = userIds,
                    limit = userIds.size,
                    offset = 0,
                ),
            )
                .map { it.id to it }
                .toMap()
        }
    }

    private fun createSearchContext() = SearchStoryContext(
        userId = requestContext.currentUser()?.id,
        deviceType = requestContext.deviceId(),
        language = requestContext.currentUser()?.language,
    )
}
