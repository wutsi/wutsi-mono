package com.wutsi.blog.client.channel

data class SearchChannelResponse(
    val channels: List<ChannelDto> = emptyList(),
)
