package com.wutsi.blog.sdk

import com.wutsi.blog.client.follower.CountFollowerResponse
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse

interface FollowerApi {
    fun create(request: CreateFollowerRequest): CreateFollowerResponse
    fun search(request: SearchFollowerRequest): SearchFollowerResponse
    fun count(request: SearchFollowerRequest): CountFollowerResponse
    fun delete(id: Long)
}
