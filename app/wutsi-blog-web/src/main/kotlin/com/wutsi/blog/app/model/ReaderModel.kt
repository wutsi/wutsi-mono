package com.wutsi.blog.app.model

class ReaderModel(
    val id: Long = -1,
    val storyId: Long = -1,
    val userId: Long = -1,
    val liked: Boolean = false,
    val commented: Boolean = false,
    val subscribed: Boolean = false,
    val user: UserModel = UserModel()
)
