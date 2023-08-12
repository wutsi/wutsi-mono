package com.wutsi.ml.personalize.endpoint

import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryResponse
import com.wutsi.ml.personalize.dto.Story
import com.wutsi.ml.personalize.service.PersonalizeV1Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SortStoryQuery(
    private val service: PersonalizeV1Service,
) {
    @PostMapping("/v1/personalize/queries/sort")
    fun sort(@RequestBody request: SortStoryRequest): SortStoryResponse {
        val result = service.sort(request)
        return SortStoryResponse(
            stories = result.map {
                Story(
                    id = it.first,
                    score = it.second,
                )
            },
        )
    }
}
