package com.wutsi.blog.mail.service.ses

data class SESNotification(
    val notificationType: String? = null,
    val mail: SESMail = SESMail(),
    val bounce: SESBounce? = null,
    val complaint: SESComplaint? = null,
    val type: String? = null,
    val subscribeURL: String? = null,
)
