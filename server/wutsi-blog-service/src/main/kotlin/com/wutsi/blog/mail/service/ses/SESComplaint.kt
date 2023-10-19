package com.wutsi.blog.mail.service.ses

data class SESComplaint(
    val complainedRecipients: List<SESRecipient> = emptyList(),
    val complaintFeedbackType: String = "",
    val userAgent: String = "",
    val feedbackId: String = "",
    val timestamp: String = "",
)
