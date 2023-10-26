package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.ValidateStoryWPPEligibilityResponse
import com.wutsi.blog.story.service.StoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ValidateStoryWPPEligibilityQuery(
    private val service: StoryService,
) {
    @GetMapping("/v1/stories/queries/validate-wpp-eligibility")
    fun get(@RequestParam("story-id") storyId: Long): ValidateStoryWPPEligibilityResponse =
        ValidateStoryWPPEligibilityResponse(
            validation = service.validateWPPEligibility(storyId)
        )
}
