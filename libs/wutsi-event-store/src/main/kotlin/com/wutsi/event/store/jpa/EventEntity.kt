package com.wutsi.event.store.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "T_EVENT")
data class EventEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val streamId: Long = -1,
    val entityId: String = "",
    val userId: String? = null,
    val deviceId: String? = null,
    val type: String = "",
    val version: Long = -1,
    val payload: String? = null,
    val metadata: String? = null,
    val timestamp: Date = Date(),
)
