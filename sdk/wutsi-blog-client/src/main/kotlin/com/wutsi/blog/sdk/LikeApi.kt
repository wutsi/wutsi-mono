package com.wutsi.blog.sdk

import com.wutsi.blog.client.like.CountLikeResponse
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.CreateLikeResponse
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.client.like.SearchLikeResponse

interface LikeApi {
    fun create(request: CreateLikeRequest): CreateLikeResponse
    fun search(request: SearchLikeRequest): SearchLikeResponse
    fun count(request: SearchLikeRequest): CountLikeResponse
    fun delete(id: Long)
}
