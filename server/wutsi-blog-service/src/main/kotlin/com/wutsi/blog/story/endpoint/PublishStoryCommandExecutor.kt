package com.wutsi.blog.story.endpoint

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.service.StoryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/stories/commands/publish")
class PublishStoryCommandExecutor(
    private val storyService: StoryService,
    private val securityManager: SecurityManager,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: PublishStoryCommand) {
        securityManager.checkStoryOwnership(command.storyId)
        storyService.publish(command)
    }
}
