package com.wutsi.blog.sdk

import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.comment.SearchCommentResponse

interface CommentApi {
    fun create(request: CreateCommentRequest): CreateCommentResponse
    fun search(request: SearchCommentRequest): SearchCommentResponse
    fun count(request: SearchCommentRequest): CountCommentResponse
    fun delete(id: Long)
}
