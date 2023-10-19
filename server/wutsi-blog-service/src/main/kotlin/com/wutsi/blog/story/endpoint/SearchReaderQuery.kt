package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.Reader
import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.story.dto.SearchReaderResponse
import com.wutsi.blog.story.service.ReaderService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchReaderQuery(
    private val service: ReaderService,
) {
    @PostMapping("/v1/readers/queries/search")
    fun create(@Valid @RequestBody request: SearchReaderRequest) = SearchReaderResponse(
        readers = service.search(request).map {
            Reader(
                id = it.id ?: -1,
                userId = it.userId,
                storyId = it.storyId,
                liked = it.liked,
                subscribed = it.subscribed,
                commented = it.commented,
                email = it.email,
            )
        },
    )
}
