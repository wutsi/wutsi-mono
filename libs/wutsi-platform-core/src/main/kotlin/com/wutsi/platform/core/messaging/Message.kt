package com.wutsi.platform.core.messaging

data class Message(
    val sender: Party? = null,
    val recipient: Party,
    val subject: String? = null,
    val body: String = "",
    val language: String? = null,
    val mimeType: String = "text/plain",
    val imageUrl: String? = null,

    /**
     * For SMS only: URL to append to the message
     */
    val url: String? = null,

    /**
     * For Push notification: Data to send with message
     */
    val data: Map<String, String> = emptyMap(),
)
