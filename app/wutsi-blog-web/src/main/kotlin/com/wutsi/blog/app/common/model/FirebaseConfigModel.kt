package com.wutsi.blog.app.common.model

data class FirebaseConfigModel(
    val apiKey: String = "",
    val projectId: String = "",
    val appId: String = "",
    val publicVapidKey: String = "",
    val senderId: String = "",
)
