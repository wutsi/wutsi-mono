package com.wutsi.blog.story.endpoint

import com.wutsi.blog.security.service.SecurityManager
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.service.StoryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/v1/stories/commands/unpublish")
class UnpublishStoryCommandExecutor(
    private val service: StoryService,
    private val securityManager: SecurityManager,
) {
    @PostMapping()
    fun create(@RequestBody @Valid command: UnpublishStoryCommand) {
        securityManager.checkStoryOwnership(command.storyId)
        service.unpublish(command)
    }
}
