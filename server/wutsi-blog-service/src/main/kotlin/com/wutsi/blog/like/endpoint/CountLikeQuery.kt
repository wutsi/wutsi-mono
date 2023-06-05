package com.wutsi.blog.like.endpoint

import com.wutsi.blog.like.dto.CountLikeRequest
import com.wutsi.blog.like.dto.CountLikeResponse
import com.wutsi.blog.like.service.LikeService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/likes/queries/count")
class CountLikeQuery(
    private val service: LikeService,
) {
    @PostMapping
    fun count(@Valid @RequestBody request: CountLikeRequest): CountLikeResponse =
        service.count(request)
}
