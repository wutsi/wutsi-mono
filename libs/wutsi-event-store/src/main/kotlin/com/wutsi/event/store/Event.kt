package com.wutsi.event.store

data class Event(
    val streamId: Long,
    val type: String,
    val entityId: String,
    val userId: String? = null,
    val deviceId: String? = null,
    val payload: Any? = null,
    val metadata: Map<String, Any?>? = null,
)
