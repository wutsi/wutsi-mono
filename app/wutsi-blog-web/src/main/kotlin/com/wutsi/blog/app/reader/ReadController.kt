package com.wutsi.blog.app.reader

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.story.AbstractStoryReadController
import com.wutsi.blog.app.reader.schemas.StorySchemasGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.editorjs.json.EJSJsonReader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

@Controller
class ReadController(
    private val schemas: StorySchemasGenerator,

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
        loadPage(id, model)
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

    @GetMapping("/read/{id}/recommend")
    fun recommend(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "summary") layout: String = "summary",
        model: Model,
    ): String {
        try {
            val stories = service.recommend(id, 20)
            model.addAttribute("stories", stories.take(5))
            if (stories.isNotEmpty()) {
                val story = service.get(id)
                model.addAttribute("blog", story.user)
                model.addAttribute("layout", layout)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find Story recommendations", ex)
        }
        return "reader/fragment/recommend"
    }
}
