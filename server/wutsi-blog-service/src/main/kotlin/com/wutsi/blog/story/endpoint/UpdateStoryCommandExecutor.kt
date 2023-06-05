package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.story.service.StoryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/stories/commands/update")
class UpdateStoryCommandExecutor(private val service: StoryService) {
    @PostMapping()
    fun create(@RequestBody @Valid command: UpdateStoryCommand) {
        service.update(command)
    }
}
