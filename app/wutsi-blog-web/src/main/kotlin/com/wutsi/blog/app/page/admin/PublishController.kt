package com.wutsi.blog.app.page.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class PublishController {
    @GetMapping("/me/story/{id}/publish")
    fun index(@PathVariable id: Long): String {
        return "redirect:/me/story/$id/readability"
    }
}
