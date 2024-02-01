package com.wutsi.blog.mail.service.model

data class StoryEarningModel(
    val id: Long = -1,
    val title: String = "",
    val wppScore: String = "",
    val earnings: String = "",
    val bonus: String = "",
    val total: String = "",
)
