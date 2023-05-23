package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class StoryController(
    private val service: StoryService,
    private val requestContext: RequestContext,
) {
    @GetMapping("/story/count")
    @ResponseBody
    fun count(@RequestParam status: StoryStatus): Map<String, Int> {
        val count = service.count(status)
        return mapOf("count" to count)
    }
}
