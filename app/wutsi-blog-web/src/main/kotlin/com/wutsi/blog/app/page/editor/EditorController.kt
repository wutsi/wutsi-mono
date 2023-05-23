package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.page.story.model.StoryForm
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.security.model.Permission
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
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/editor")
    fun create(model: Model): String {
        model.addAttribute("storyId", 0)
        return "page/editor/index"
    }

    @GetMapping("/editor/{id}")
    fun update(@PathVariable id: Long, @RequestParam error: String? = null, model: Model): String {
        model.addAttribute("storyId", id)
        model.addAttribute("error", error)
        return "page/editor/index"
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
}
