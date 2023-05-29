package com.wutsi.blog.app.model

import com.wutsi.blog.app.page.settings.model.UserModel

data class CommentModel(
    val id: Long = -1,
    val user: UserModel? = UserModel(),
    val text: String = "",
    val timestamp: String = "",
)
