package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.backend.MailBackend
import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.backend.ShareBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.form.PublishForm
import com.wutsi.blog.app.mapper.StoryMapper
import com.wutsi.blog.app.model.ReadabilityModel
import com.wutsi.blog.app.model.StoryForm
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.ejs.EJSFilterSet
import com.wutsi.blog.app.service.ejs.EJSInterceptorSet
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.tracing.TracingContext
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
    private val ejsIntercetors: EJSInterceptorSet,
    private val userService: UserService,
    private val storyBackend: StoryBackend,
    private val likeBackend: LikeBackend,
    private val pinBackend: PinBackend,
    private val shareBackend: ShareBackend,
    private val mailBackend: MailBackend,
    private val tracingContext: TracingContext,
) {
    fun save(editor: StoryForm): StoryForm {
        val storyId = if (shouldCreate(editor)) {
            storyBackend.create(
                CreateStoryCommand(
                    userId = requestContext.currentUser()?.id,
                    title = editor.title,
                    content = editor.content,
                ),
            ).storyId
        } else {
            storyBackend.update(
                UpdateStoryCommand(
                    storyId = editor.id!!,
                    title = editor.title,
                    content = editor.content,
                ),
            )
            editor.id
        }

        return StoryForm(
            id = storyId,
            title = editor.title,
            content = editor.content,
        )
    }

    fun get(id: Long): StoryModel {
        val story = storyBackend.get(id).story
        val user = userService.get(story.userId)
        return mapper.toStoryModel(story, user)
    }

    fun search(request: SearchStoryRequest, pinStoryId: Long? = null): List<StoryModel> {
        val stories = storyBackend.search(request).stories
        if (stories.isEmpty()) {
            return emptyList()
        }

        val users = searchUserMap(stories)

        return stories.map {
            mapper.toStoryModel(it, users[it.userId], pinStoryId)
        }
    }

    fun searchSimilar(request: SearchSimilarStoryRequest): List<StoryModel> {
        val storyIds = storyBackend.searchSimilar(request).storyIds
        if (storyIds.isEmpty()) {
            return emptyList()
        }

        return search(
            SearchStoryRequest(
                storyIds = storyIds,
                status = StoryStatus.PUBLISHED,
                limit = storyIds.size,
                bubbleDownViewedStories = true
            )
        )
    }

    fun generateHtmlContent(story: StoryModel, summary: Boolean = false): String {
        if (story.content == null) {
            return ""
        }

        // EJS
        val ejs = ejsJsonReader.read(story.content, summary)
        ejsIntercetors.filter(ejs, story)

        // HTML
        val html = StringWriter()
        ejsHtmlWriter.write(ejs, html)

        val doc = Jsoup.parse(html.toString())
        ejsFilters.filter(doc)
        return doc.html()
    }

    fun publish(form: PublishForm) {
        storyBackend.publish(
            PublishStoryCommand(
                storyId = form.id,
                title = form.title,
                tagline = form.tagline,
                summary = form.summary,
                topicId = form.topicId.toLong(),
                tags = form.tags,
                access = form.access,
                scheduledPublishDateTime = if (form.publishNow) {
                    null
                } else {
                    SimpleDateFormat("yyyy-MM-dd").parse(form.scheduledPublishDate)
                },
            ),
        )
    }

    fun unpublish(storyId: Long) {
        storyBackend.unpublish(UnpublishStoryCommand(storyId))
    }

    fun import(url: String): Long =
        storyBackend.import(
            ImportStoryCommand(
                url = url,
                userId = requestContext.currentUser()?.id ?: -1,
            ),
        ).storyId

    fun readability(id: Long): ReadabilityModel {
        val result = storyBackend.readability(id).readability
        return mapper.toReadabilityModel(result)
    }

    fun delete(id: Long) {
        storyBackend.delete(DeleteStoryCommand(id))
    }

    fun like(storyId: Long) {
        likeBackend.like(
            LikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun unlike(storyId: Long) {
        likeBackend.unlike(
            UnlikeStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
                deviceId = tracingContext.deviceId(),
            ),
        )
    }

    fun share(storyId: Long) {
        shareBackend.share(
            ShareStoryCommand(
                storyId = storyId,
                userId = requestContext.currentUser()?.id,
            ),
        )
    }

    fun pin(storyId: Long) {
        pinBackend.pin(
            PinStoryCommand(
                storyId = storyId,
            ),
        )
    }

    fun unpin(storyId: Long) {
        pinBackend.unpin(
            UnpinStoryCommand(storyId),
        )
    }

    fun view(storyId: Long, readTimeMillis: Long) =
        view(storyId, requestContext.currentUser()?.id, readTimeMillis)

    fun view(storyId: Long, userId: Long?, readTimeMillis: Long) {
        storyBackend.view(
            ViewStoryCommand(
                storyId = storyId,
                deviceId = tracingContext.deviceId(),
                userId = userId,
                readTimeMillis = readTimeMillis,
            ),
        )
    }

    fun sendDailyMail(storyId: Long) {
        mailBackend.sendDaily(
            SendStoryDailyEmailCommand(storyId),
        )
    }

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

    private fun searchUserMap(stories: List<StorySummary>): Map<Long, UserModel?> {
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
            ).associateBy { it.id }
        }
    }
}
