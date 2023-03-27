package com.wutsi.application.checkout.settings.account.entity

import java.io.Serializable

data class AccountEntity(
    val number: String = "",
    val providerId: Long = -1,
    val ownerName: String = "",
    val type: String = "",
    var otpToken: String = "",
) : Serializable
