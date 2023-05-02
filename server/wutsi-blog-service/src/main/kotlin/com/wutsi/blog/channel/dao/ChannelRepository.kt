package com.wutsi.blog.channel.dao

import com.wutsi.blog.channel.domain.Channel
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ChannelRepository : CrudRepository<Channel, Long> {
    fun findByUserId(userId: Long): List<Channel>
    fun findByUserIdAndType(userId: Long, type: ChannelType): Optional<Channel>
    fun findByProviderUserIdAndType(providerUserId: String, type: ChannelType): Optional<Channel>
    fun findByType(type: ChannelType): List<Channel>
}
