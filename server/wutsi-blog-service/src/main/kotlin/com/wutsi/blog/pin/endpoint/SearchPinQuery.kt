package com.wutsi.blog.pin.endpoint

import com.wutsi.blog.pin.dto.PinStory
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.dto.SearchPinResponse
import com.wutsi.blog.pin.service.PinService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/pins/queries/search")
class SearchPinQuery(
    private val service: PinService,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: SearchPinRequest,
    ): SearchPinResponse {
        val stories = service.search(request)
        return SearchPinResponse(
            pins = stories.map {
                PinStory(
                    storyId = it.storyId,
                    userId = it.userId,
                    timestamp = it.timestamp,
                )
            },
        )
    }
}
