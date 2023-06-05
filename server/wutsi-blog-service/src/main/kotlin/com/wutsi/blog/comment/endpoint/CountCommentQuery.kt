package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.comment.dto.CountCommentResponse
import com.wutsi.blog.comment.service.CommentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/comments/queries/count")
class CountCommentQuery(
    private val service: CommentService,
) {
    @PostMapping
    fun search(@Valid @RequestBody request: CountCommentRequest): CountCommentResponse =
        service.count(request)
}
