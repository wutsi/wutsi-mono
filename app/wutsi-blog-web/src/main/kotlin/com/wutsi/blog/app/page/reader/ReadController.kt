package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.AbstractStoryReadController
import com.wutsi.blog.app.page.reader.schemas.StorySchemasGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
class ReadController(
    private val schemas: StorySchemasGenerator,
    private val logger: KVLogger,
    private val trackingBackend: TrackingBackend,
    private val tracingContext: TracingContext,

    ejsJsonReader: EJSJsonReader,
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryReadController(ejsJsonReader, service, requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReadController::class.java)
        const val MAX_RECOMMENDATIONS = 5
        const val FROM_PRE_SUBSCRIBE = "pre_subscribe"
        const val FACEBOOK_CLICK_ID = "fbclid"
    }

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    @GetMapping("/read/{id}/{title}")
    fun read(
        @PathVariable id: String,
        @PathVariable title: String,
        @RequestParam(required = false) like: String? = null,
        @RequestParam(required = false, name = "like-key") likeKey: String? = null,
        @RequestParam(required = false, name = "utm_from") from: String? = null,
        model: Model,
    ): String {
        return read(id, model, like, likeKey, from)
    }

    @GetMapping("/read/{id}")
    fun read(
        @PathVariable id: String,
        model: Model,
        @RequestParam(required = false) like: String? = null,
        @RequestParam(required = false, name = "like-key") likeKey: String? = null,
        @RequestParam(required = false, name = "utm_from") from: String? = null,
    ): String {
        try {
            val storyId = id.toLong()

            // Load the story
            val story = loadPage(storyId, model)

            // Should subscribe?
            if (shouldPreSubscribe(story, from)) {
                return "reader/story_pre_subscribe"
            }

            // Like
            if (like == "1" && like(storyId, likeKey)) {
                // OK
            }

            loadRecommendations(story, model)
            preSubscribed(story, from)
            return "reader/read"
        } catch (ex: HttpClientErrorException) {
            if (ex.statusCode == HttpStatus.NOT_FOUND) {
                logger.add("not_found", true)
                logger.add("not_found_error", ex.message)
                return notFound(model)
            } else {
                throw ex
            }
        } catch (ex: ForbiddenException) {
            return notFound(model)
        }
    }

    private fun shouldPreSubscribe(story: StoryModel, from: String?): Boolean =
        !story.user.subscribed && // User not subscribed
            story.user.id != requestContext.currentUser()?.id && // User is not author
            (
                from == BlogController.FROM || // User come from BLOG
                    from == InboxController.FROM || // User come from INBOX ||
                    !requestContext.request.getParameter(FACEBOOK_CLICK_ID).isNullOrEmpty() // User come from facebook
                ) &&
            CookieHelper.get(preSubscribeKey(story), requestContext.request).isNullOrEmpty() // Control frequency

    private fun preSubscribed(story: StoryModel, from: String?) {
        if (from == FROM_PRE_SUBSCRIBE) {
            val key = preSubscribeKey(story)
            CookieHelper.put(key, "1", requestContext.request, requestContext.response, CookieHelper.ONE_DAY_SECONDS)
        }
    }

    private fun preSubscribeKey(story: StoryModel) = "_w_psb-${story.user.id}"

    private fun notFound(model: Model): String {
        model.addAttribute(
            "page",
            createPage(
                name = PageName.STORY_NOT_FOUND,
                title = requestContext.getMessage("page.home.metadata.title"),
                description = requestContext.getMessage("page.home.metadata.description"),
                robots = "noindex,nofollow",
            ),
        )

        val stories = service.search(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                sortOrder = SortOrder.DESCENDING,
                dedupUser = true,
                limit = 10,
            ),
        ).map { it.copy(slug = "${it.slug}?utm_from=not-found") }
        if (stories.isNotEmpty()) {
            model.addAttribute("stories", stories)
        }
        return "reader/story_not_found"
    }

    private fun like(id: Long, key: String?): Boolean {
        key ?: return false

        try {
            val parts = key.split("_") // First part is dummy GUID
            val storyId = parts[1].toLong()
            if (storyId == id) {
                service.like(storyId)
                return true
            } else {
                logger.add("invalid_like_key", true)
            }
        } catch (ex: Exception) { // Never fail on error
            LOGGER.warn("Unable to like to Story#$id with Key=$key", ex)
        }
        return false
    }

    @ResponseBody
    @PostMapping("/read/{id}/like")
    fun like(@PathVariable id: Long) {
        service.like(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/unlike")
    fun unlike(@PathVariable id: Long) {
        service.unlike(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/pin")
    fun pin(@PathVariable id: Long) {
        service.pin(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/unpin")
    fun unpin(@PathVariable id: Long) {
        service.unpin(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/share")
    fun share(@PathVariable id: Long) {
        service.share(id)
    }

    @ResponseBody
    @PostMapping("/read/{id}/track")
    fun track(@PathVariable id: Long, @RequestBody form: TrackForm): Map<String, String> {
        val user = requestContext.currentUser()
        if (user?.superUser == true || service.get(id).id == user?.id) {
            logger.add("track_ignored", true)
            return emptyMap()
        }

        trackingBackend.push(
            PushTrackRequest(
                time = form.time,
                correlationId = form.hitId,
                productId = id.toString(),
                event = form.event,
                deviceId = tracingContext.deviceId(),
                url = form.url,
                ua = form.ua,
                value = form.value,
                page = PageName.READ,
                referrer = form.referrer,
                accountId = requestContext.currentUser()?.id?.toString(),
                ip = requestContext.remoteIp(),
            ),
        )

        if (form.event == "readend") {
            val readTime = try {
                form.value!!.toLong()
            } catch (ex: Exception) {
                -1
            }
            service.view(id, readTime)
        }
        return emptyMap()
    }

    @ResponseBody
    @PostMapping("/read/{id}/send-daily")
    fun sendDaily(@PathVariable id: Long) {
        service.sendDailyMail(id)
    }

    private fun loadRecommendations(story: StoryModel, model: Model) {
        try {
            val request = SearchStoryRequest(
                userIds = listOf(story.user.id),
                topicId = story.topic.id,
                sortBy = StorySortStrategy.RECOMMENDED,
                sortOrder = SortOrder.DESCENDING,
                status = StoryStatus.PUBLISHED,
                limit = 20,
                bubbleDownViewedStories = true,
            )
            val stories = service.search(request).filter { it.id != story.id }.toMutableList()
            if (stories.size < MAX_RECOMMENDATIONS) {
                val storyIds = stories.map { it.id }
                val supplement = service.search(request.copy(topicId = null))
                    .filter { it.id != story.id && !storyIds.contains(it.id) }
                stories.addAll(supplement)
            }

            model.addAttribute(
                "stories",
                stories.take(MAX_RECOMMENDATIONS)
                    .map { it.copy(slug = "${it.slug}?utm_from=read-also") },
            )
            model.addAttribute("layout", "summary")

            logger.add("recommended_stories", stories.size)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
    }
}
