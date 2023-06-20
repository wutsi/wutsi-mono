package com.wutsi.blog.app.page.admin

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.page.AbstractStoryController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ReadabilityController(
    service: StoryService,
    requestContext: RequestContext,
) : AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR_READABILITY

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/readability")
    fun index(@PathVariable id: Long, model: Model): String {
        val story = getStory(id)
        model.addAttribute("story", story)

        val readability = service.readability(id)
        model.addAttribute("readability", readability)

        model.addAttribute("canPublish", story.readabilityScore > readability.scoreThreshold)

        return "admin/readability"
    }
}
