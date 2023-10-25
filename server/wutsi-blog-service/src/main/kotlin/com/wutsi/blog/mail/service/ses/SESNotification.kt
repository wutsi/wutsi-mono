package com.wutsi.blog.mail.service.ses

data class SESNotification(
    val type: String? = null,
    val notificationType: String? = null,
    val mail: SESMail = SESMail(),
    val bounce: SESBounce? = null,
    val complaint: SESComplaint? = null,
)
