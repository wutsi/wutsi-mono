package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.job.StoryEmailNotificationJob
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/send-notification")
class SendStoryEmailNotification(
    private val job: StoryEmailNotificationJob,
) {
    @GetMapping()
    fun create() {
        job.run()
    }
}
