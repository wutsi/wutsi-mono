package com.wutsi.blog.country.dto

import com.wutsi.blog.transaction.dto.PaymentProviderType

data class PhoneNumberPrefix(
    val carrier: PaymentProviderType,
    val prefix: String,
)
