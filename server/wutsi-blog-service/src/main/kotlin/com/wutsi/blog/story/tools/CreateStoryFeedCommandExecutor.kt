package com.wutsi.blog.story.tools

import com.wutsi.blog.story.service.StoryFeedsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Deprecated("")
@RestController
@RequestMapping("/v1/stories/commands/create-feed")
class CreateStoryFeedCommandExecutor(
    private val service: StoryFeedsService,
) {
    @GetMapping
    fun execute() {
        service.generate()
    }
}
