package com.wutsi.blog.channel.domain

import com.wutsi.blog.story.domain.Story

data class SocialPost(
    val story: Story = Story(),
    val message: String? = null,
    val pictureUrl: String? = null,
    val campaign: String? = null,
    val includeLink: Boolean = true,
    val language: String? = null,
)
