package com.wutsi.blog.earning.entity

data class WPPEarnings(
    val users: List<WPPUserEntity> = emptyList(),
    val stories: List<WPPStoryEntity> = emptyList(),
)
