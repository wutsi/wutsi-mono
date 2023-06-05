package com.wutsi.blog.channel.domain

import com.wutsi.blog.story.domain.StoryEntity

data class SocialPost(
    val story: StoryEntity = StoryEntity(),
    val message: String? = null,
    val pictureUrl: String? = null,
    val campaign: String? = null,
    val includeLink: Boolean = true,
    val language: String? = null,
)
