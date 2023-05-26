package com.wutsi.blog.app.model

data class TranslationModel(
    val story: StoryModel = StoryModel(),
    val html: String = "",
)
