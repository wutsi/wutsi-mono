package com.wutsi.blog.channel.service

import com.wutsi.blog.channel.domain.SocialPost
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.story.domain.StoryEntity

interface ChannelPublisher {
    fun type(): ChannelType

    fun publishStory(story: StoryEntity)

    fun publishPost(socialPost: SocialPost): String
}
