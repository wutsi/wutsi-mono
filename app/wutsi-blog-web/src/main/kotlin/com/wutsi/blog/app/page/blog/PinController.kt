package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.service.PinService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PinController(
    private val service: PinService,
    private val requestContext: RequestContext,
) {
    @GetMapping("/pin/add")
    fun create(@RequestParam storyId: Long): String {
        service.create(storyId)
        return "redirect:" + requestContext.currentUser()!!.slug
    }

    @GetMapping("/pin/remove")
    fun delete(): String {
        service.delete()
        return "redirect:" + requestContext.currentUser()!!.slug
    }
}
