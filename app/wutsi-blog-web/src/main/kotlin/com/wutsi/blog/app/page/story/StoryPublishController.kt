package com.wutsi.blog.app.page.story

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StoryPublishController {
    @GetMapping("/me/story/{id}/publish")
    fun index(@PathVariable id: Long): String {
        return "redirect:/me/story/$id/readability"
    }
}
