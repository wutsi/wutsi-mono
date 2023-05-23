package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.model.TagModel
import com.wutsi.blog.app.page.story.service.TagService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/tag")
class TagController(
    private val service: TagService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORY_DRAFT

    @ResponseBody()
    @GetMapping("/search", produces = ["application/json"])
    fun search(@RequestParam(name = "q") query: String): List<TagModel> {
        return service.search(query)
    }
}
