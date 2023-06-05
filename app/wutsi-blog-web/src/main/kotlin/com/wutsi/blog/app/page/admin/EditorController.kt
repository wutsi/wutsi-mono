package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryForm
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.page.admin.model.EJSLinkResponse
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.service.LinkExtractorProvider
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class EditorController(
    service: StoryService,
    requestContext: RequestContext,
    private val provider: LinkExtractorProvider,
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/editor")
    fun create(model: Model): String =
        update(0, null, model)

    @GetMapping("/editor/{id}")
    fun update(@PathVariable id: Long, @RequestParam error: String? = null, model: Model): String {
        model.addAttribute("storyId", id)
        model.addAttribute("error", error)
        return "admin/editor"
    }

    @ResponseBody
    @GetMapping("/editor/fetch/{id}", produces = ["application/json"])
    fun fetch(@PathVariable id: Long): StoryModel {
        return getStory(id)
    }

    @ResponseBody
    @PostMapping("/editor/save", produces = ["application/json"], consumes = ["application/json"])
    fun save(@RequestBody editor: StoryForm): StoryForm {
        return service.save(editor)
    }

    @ResponseBody
    @GetMapping(value = ["/editor/link/fetch"], produces = ["application/json"])
    fun fetch(@RequestParam url: String): EJSLinkResponse {
        val meta = provider.get(url).extract(url)
        return EJSLinkResponse(
            success = 1,
            meta = meta,
        )
    }
}
