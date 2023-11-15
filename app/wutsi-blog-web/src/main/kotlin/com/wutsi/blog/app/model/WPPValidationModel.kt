package com.wutsi.blog.app.model

data class WPPValidationModel(
    val readabilityRule: Boolean = false,
    val thumbnailRule: Boolean = false,
    val wordCountRule: Boolean = false,
    val subscriptionRule: Boolean = false,
    val storyCountRule: Boolean = false,
    val blogAgeRule: Boolean = false,
    val score: Int = 0,
    val color: String = "",
)
