package com.wutsi.blog.story

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.sort.SortService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/story")
class StoryController(
    private val storyService: StoryService,
    private val sortService: SortService,
) {
    @PostMapping("/count")
    fun count(@RequestBody @Valid request: SearchStoryRequest): CountStoryResponse =
        storyService.count(request)

    @GetMapping("/{id}/readability")
    fun readability(@PathVariable id: Long): GetStoryReadabilityResponse =
        storyService.readability(id)

    @PostMapping("/sort")
    fun sort(@RequestBody @Valid request: SortStoryRequest): SortStoryResponse =
        sortService.sort(request)
}
