package com.wutsi.blog.channel.mapper

import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelDto
import org.springframework.stereotype.Service

@Service
class ChannelMapper {
    fun toChannelDto(channel: Channel) = ChannelDto(
        id = channel.id!!,
        providerUserId = channel.providerUserId,
        name = channel.name,
        pictureUrl = channel.pictureUrl,
        userId = channel.userId,
        type = channel.type,
        modificationDateTime = channel.modificationDateTime,
        creationDateTime = channel.creationDateTime,
    )
}
