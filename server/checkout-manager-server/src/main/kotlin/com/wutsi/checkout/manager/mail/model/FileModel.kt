package com.wutsi.checkout.manager.mail.model

data class FileModel(
    val id: Long = 0,
    val name: String = "",
    val downloadUrl: String = "",
    val contentSize: String = "",
    val extensionUrl: String? = null,
)
