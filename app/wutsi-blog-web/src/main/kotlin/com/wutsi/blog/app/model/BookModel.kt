package com.wutsi.blog.app.model

import java.util.Date

data class BookModel(
    val id: Long = 0,
    val userId: Long = -1,
    val transactionId: String = "",
    val product: ProductModel = ProductModel(),
    val author: UserModel = UserModel(),
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val location: String? = null,
    val readPercentage: Int = 0,
    val expiryDate: Date? = null,
    val expiryDateText: String? = null
) {
    companion object {
        const val ONE_DAY_MILLIS = 86400000L
        const val URGENCY_DAYS = 2
    }

    val playUrl: String
        get() = "/me/play/$id"

    val expiryDays: Long?
        get() = expiryDate?.let { (expiryDate.time - System.currentTimeMillis()) / ONE_DAY_MILLIS }
    val expired: Boolean
        get() = (expiryDate != null) && (expiryDate.time < System.currentTimeMillis())

    val showExpiryDate: Boolean
        get() = expiryDays?.let { days -> days >= 0 && days <= URGENCY_DAYS } ?: false
}
