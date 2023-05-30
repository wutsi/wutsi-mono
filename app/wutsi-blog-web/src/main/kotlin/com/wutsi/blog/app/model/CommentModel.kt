package com.wutsi.blog.app.model

data class CommentModel(
    val id: Long = -1,
    val user: UserModel? = UserModel(),
    val text: String = "",
    val html: String = "",
    val timestamp: String = "",
)
