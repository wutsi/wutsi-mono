package com.wutsi.blog.story.dto

data class WPPValidation(
    val score: Int = 0,
    val readabilityRule: Boolean = false,
    val thumbnailRule: Boolean = false,
    val wordCountRule: Boolean = false,
    val subscriptionRule: Boolean = false,
    val storyCountRule: Boolean = false,
    val blogAgeRule: Boolean = false,
)
