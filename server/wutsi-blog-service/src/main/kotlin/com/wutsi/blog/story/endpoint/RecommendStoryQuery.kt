package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.service.StoryRecommendationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class RecommendStoryQuery(
    private val service: StoryRecommendationService,
) {
    @PostMapping("/v1/stories/queries/recommend")
    fun recommend(@Valid @RequestBody request: RecommendStoryRequest): RecommendStoryResponse =
        RecommendStoryResponse(
            storyIds = service.recommend(request),
        )
}
