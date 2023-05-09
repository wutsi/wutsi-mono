package com.wutsi.blog.client.channel

import java.util.Date

data class ChannelDto(
    val id: Long = -1,
    val providerUserId: String = "",
    val userId: Long? = null,
    val type: ChannelType = ChannelType.unknown,
    val name: String = "",
    val pictureUrl: String? = null,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
)
