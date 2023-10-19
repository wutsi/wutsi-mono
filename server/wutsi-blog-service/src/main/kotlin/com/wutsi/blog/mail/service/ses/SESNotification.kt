package com.wutsi.blog.mail.service.ses

data class SESNotification(
    val notificationType: String = "",
    val mail: SESMail = SESMail(),
    val bounce: SESBounce? = null,
    val complaint: SESComplaint? = null,
)
