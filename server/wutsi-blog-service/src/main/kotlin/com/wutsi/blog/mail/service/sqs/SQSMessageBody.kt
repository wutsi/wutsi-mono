package com.wutsi.blog.mail.service.sqs

data class SQSMessageBody(
    val type: String = "",
    val messageId: String = "",
    val message: String = "",
    val topicArn: String = "",
)
