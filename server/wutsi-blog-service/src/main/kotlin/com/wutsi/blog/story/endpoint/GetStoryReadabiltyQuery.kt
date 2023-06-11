package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.GetStoryReadabilityResponse
import com.wutsi.blog.user.dto.Readability
import com.wutsi.blog.user.dto.ReadabilityRule
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetStoryReadabiltyQuery(
    private val service: StoryService,
    @Value("\${wutsi.readability.score-threshold}") private val scoreThreshold: Int,
) {
    @GetMapping("/v1/stories/{id}/readability")
    fun get(@PathVariable id: Long): GetStoryReadabilityResponse {
        val result = service.readability(id)
        return GetStoryReadabilityResponse(
            readability = Readability(
                score = result.score,
                scoreThreshold = scoreThreshold,
                rules = result.ruleResults.map {
                    ReadabilityRule(
                        name = it.rule.name(),
                        score = it.score,
                    )
                },
            ),
        )
    }
}
