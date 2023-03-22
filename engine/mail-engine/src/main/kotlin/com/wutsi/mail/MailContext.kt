package com.wutsi.mail

data class MailContext(
    val merchant: Merchant,
    val assetUrl: String,
    val template: String? = null,
)
