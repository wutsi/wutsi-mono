package com.wutsi.blog.story.endpoint

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.service.StoryService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/delete")
class DeleteStoryCommandExecutor(
    private val storyService: StoryService,
    private val securityManager: SecurityManager,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: DeleteStoryCommand) {
        securityManager.checkStoryOwnership(command.storyId)

        storyService.delete(command)
    }
}
