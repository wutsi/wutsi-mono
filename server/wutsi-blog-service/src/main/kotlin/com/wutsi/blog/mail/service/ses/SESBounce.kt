package com.wutsi.blog.mail.service.ses

data class SESBounce(
    val bounceType: String = "",
    val bounceSubType: String = "",
    val bouncedRecipients: List<SESRecipient> = emptyList()
)
