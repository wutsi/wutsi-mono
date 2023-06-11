package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.form.TrackForm
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.reader.schemas.StorySchemasGenerator
import com.wutsi.blog.app.page.story.AbstractStoryReadController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

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
    }

    override fun pageName() = PageName.READ

    override fun requiredPermissions() = listOf(Permission.reader)

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun generateSchemas(story: StoryModel) = schemas.generate(story)

    @GetMapping("/read/{id}/{title}")
    fun read(
        @PathVariable id: Long,
        @PathVariable title: String,
        @RequestParam(required = false) comment: String? = null,
        model: Model,
        response: HttpServletResponse,
    ): String {
        return read(id, comment, model, response)
    }

    @GetMapping("/read/{id}")
    fun read(
        @PathVariable id: Long,
        @RequestParam(required = false) comment: String? = null,
        model: Model,
        response: HttpServletResponse,
    ): String {
        val story = loadPage(id, model)
        loadRecommendations(story, model)
        return "reader/read"
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
                referrer = requestContext.request.getHeader(HttpHeaders.REFERER),
                accountId = requestContext.currentUser()?.id?.toString(),
            ),
        )
        return emptyMap()
    }

    private fun loadRecommendations(story: StoryModel, model: Model) {
        try {
            val stories = service.search(
                request = SearchStoryRequest(
                    userIds = listOf(story.user.id),
                    sortBy = StorySortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                    limit = 20,
                ),
            ).filter { it.id != story.id }.take(5)
            model.addAttribute("stories", stories)
            model.addAttribute("layout", "summary")

            logger.add("recommended_stories", stories.size)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
    }
}
