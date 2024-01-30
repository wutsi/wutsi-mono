package com.wutsi.blog.transaction.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_TRANSACTION_EVENT")
data class TransactionEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val transactionId: String = "",
    val method: String = "",
    val uri: String = "",
    val statusCode: Int? = null,
    val request: String? = null,
    val response: String? = null,

    val creationDateTime: Date = Date(),
)
