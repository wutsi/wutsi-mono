package com.wutsi.blog.channel.service

import com.wutsi.blog.channel.domain.SocialPost
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.story.domain.Story

interface ChannelPublisher {
    fun type(): ChannelType

    fun publishStory(story: Story)

    fun publishPost(socialPost: SocialPost): String
}
