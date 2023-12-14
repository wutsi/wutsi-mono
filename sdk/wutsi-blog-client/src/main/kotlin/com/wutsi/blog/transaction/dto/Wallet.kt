package com.wutsi.blog.transaction.dto

import java.util.Date

data class Wallet(
    val id: String = "",
    val userId: Long = -1,
    val country: String = "",
    val currency: String = "",
    var balance: Long = 0,
    var donationCount: Long = 0,
    var chargeCount: Long = 0,
    val creationDateTime: Date = Date(),
    var lastModificationDateTime: Date = Date(),
    var lastCashoutDateTime: Date? = null,
    var nextCashoutDate: Date? = null,
    var account: WalletAccount? = null,
)
