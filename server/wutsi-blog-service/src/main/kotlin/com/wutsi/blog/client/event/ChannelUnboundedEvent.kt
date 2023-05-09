package com.wutsi.blog.client.event

import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.ChannelType.unknown

data class ChannelUnboundedEvent(
    val channelId: Long = -1,
    val userId: Long = -1,
    val channelType: ChannelType = unknown,
)
