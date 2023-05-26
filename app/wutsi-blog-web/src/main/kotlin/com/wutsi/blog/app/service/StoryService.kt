package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.mapper.StoryMapper
import com.wutsi.blog.app.model.StoryForm
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.editor.model.PublishForm
import com.wutsi.blog.app.page.editor.model.ReadabilityModel
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.like.dto.Like
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
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import org.jsoup.Jsoup
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
    private val backend: StoryBackend,
    private val likeService: LikeService,
) {
    fun save(editor: StoryForm): StoryForm {
        var response = SaveStoryResponse()
        val request = toSaveStoryRequest(editor)
        if (shouldCreate(editor)) {
            response = backend.create(request)
        } else if (shouldUpdate(editor)) {
            response = backend.update(editor.id!!, request)
        }

        return StoryForm(
            id = response.storyId,
            title = editor.title,
            content = editor.content,
        )
    }

    fun get(id: Long): StoryModel {
        val story = backend.get(id).story
        val likes = getLikes(listOf(id))
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user, likes)
    }

    @Deprecated("")
    fun translate(id: Long, language: String): StoryModel {
        val story = backend.translate(id, language).story
        val likes = getLikes(listOf(id))
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user, likes)
    }

    fun search(
        request: SearchStoryRequest,
        pin: PinModel? = null,
        bubbleDownIds: List<Long> = emptyList(),
    ): List<StoryModel> {
        val stories = bubbleDown(backend.search(request).stories, bubbleDownIds)
        if (stories.isEmpty()) {
            return emptyList()
        }

        val likes = getLikes(
            storyIds = stories.map { it.id }
        )
        val users = searchUserMap(stories)
        return stories.map { mapper.toStoryModel(it, users[it.userId], pin, likes) }
    }

    private fun getLikes(storyIds: List<Long>): List<Like> =
        try {
            likeService.search(storyIds)
        } catch (ex: Exception) {
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

    fun publish(editor: PublishForm) {
        backend.publish(
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
        return backend.count(request).total
    }

    fun import(url: String): Long {
        val request = ImportStoryRequest(
            url = url,
            accessToken = requestContext.accessToken(),
            siteId = requestContext.siteId(),
        )
        return backend.import(request).storyId
    }

    fun readability(id: Long): ReadabilityModel {
        val result = backend.readability(id).readability
        return mapper.toReadabilityModel(result)
    }

    fun delete(id: Long) {
        backend.delete(id)
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

    fun recommend(storyId: Long, limit: Int = 20): List<StoryModel> {
        val stories = backend.recommend(
            RecommendStoryRequest(
                storyId = storyId,
                limit = limit,
                context = createSearchContext(),
            ),
        ).stories.filter { it.id != storyId }
        val users = searchUserMap(stories)
        return stories.map { mapper.toStoryModel(it, users[it.userId], null, emptyList()) }
    }

    fun createSearchContext() = SearchStoryContext(
        userId = requestContext.currentUser()?.id,
        deviceType = requestContext.deviceId(),
        language = requestContext.currentUser()?.language,
    )
}
