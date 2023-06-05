package com.wutsi.blog.channel.service

import com.wutsi.blog.channel.domain.SocialPost
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.ChannelType.unknown
import com.wutsi.blog.story.domain.StoryEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
open class ChannelRegistry : ChannelPublisher {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ChannelRegistry::class.java)
    }

    private val channels: MutableList<ChannelPublisher> = mutableListOf()

    fun register(channel: ChannelPublisher) {
        channels.add(channel)
    }

    fun channelCount(): Int = channels.size

    open fun getChannelPublisher(type: ChannelType): ChannelPublisher =
        channels.filter { it.type() == type }
            .first()

    override fun publishStory(story: StoryEntity) {
        channels.forEach {
            try {
                it.publishStory(story)
            } catch (ex: Exception) {
                LOGGER.info("Unable to publish Story#${story.id} to ${it.type()}", ex)
            }
        }
    }

    override fun type(): ChannelType = unknown

    override fun publishPost(socialPost: SocialPost): String {
        channels.forEach {
            try {
                it.publishPost(socialPost)
            } catch (ex: Exception) {
                LOGGER.info("Unable to publish Story#${socialPost.story.id} to ${it.type()}", ex)
            }
        }
        return "-"
    }
}
