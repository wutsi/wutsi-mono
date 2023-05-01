package com.wutsi.blog.sdk

import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.channel.GetChannelResponse
import com.wutsi.blog.client.channel.SearchChannelResponse

interface ChannelApi {
    fun create(request: CreateChannelRequest): CreateChannelResponse
    fun get(userId: Long): GetChannelResponse
    fun search(userId: Long): SearchChannelResponse
    fun delete(id: Long)
}
