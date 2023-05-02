package com.wutsi.blog.channel.service

import com.wutsi.blog.channel.dao.ChannelRepository
import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class ChannelService(
    private val dao: ChannelRepository,
) {
    fun findById(id: Long): Channel =
        dao.findById(id)
            .orElseThrow { NotFoundException(Error("channel_not_found")) }

    @Transactional
    fun create(request: CreateChannelRequest): Channel {
        val now = Date()
        val channel = Channel(
            userId = request.userId!!,
            providerUserId = request.providerUserId,
            accessToken = request.accessToken,
            accessTokenSecret = request.accessTokenSecret,
            name = request.name,
            type = request.type,
            pictureUrl = request.pictureUrl,
            creationDateTime = now,
            modificationDateTime = now,
        )
        return dao.save(channel)
    }

    @Transactional
    fun delete(id: Long): Channel {
        val channel = findById(id)
        dao.delete(channel)
        return channel
    }

    fun findByType(type: ChannelType) = dao.findByType(type)

    fun search(userId: Long): List<Channel> =
        dao.findByUserId(userId)

    fun findChannel(providerUserId: String, type: ChannelType): Channel =
        dao.findByProviderUserIdAndType(providerUserId, type)
            .orElseThrow { NotFoundException(Error("channel_not_found")) }

    fun findByUserAndType(userId: Long, type: ChannelType): Channel =
        dao.findByUserIdAndType(userId, type)
            .orElseThrow { NotFoundException(Error("channel_not_found")) }
}
