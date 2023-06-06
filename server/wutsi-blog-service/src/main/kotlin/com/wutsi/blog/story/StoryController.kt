package com.wutsi.blog.story

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/story")
class StoryController(
    private val storyService: StoryService,
) {
    @PostMapping("/count")
    fun count(@RequestBody @Valid request: SearchStoryRequest): CountStoryResponse =
        storyService.count(request)
}
