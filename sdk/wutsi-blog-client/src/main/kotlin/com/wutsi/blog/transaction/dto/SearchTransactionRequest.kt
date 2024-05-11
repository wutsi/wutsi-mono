package com.wutsi.blog.transaction.dto

import com.wutsi.platform.payment.core.Status
import java.util.Date

data class SearchTransactionRequest(
    val walletId: String? = null,
    val storeId: String? = null,
    val userId: Long? = null,
    val email: String? = null,
    val productIds: List<Long> = emptyList(),
    val adsIds: List<Long> = emptyList(),
    val statuses: List<Status> = emptyList(),
    val types: List<TransactionType> = emptyList(),
    val creationDateTimeFrom: Date? = null,
    val creationDateTimeTo: Date? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
