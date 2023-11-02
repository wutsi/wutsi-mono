package com.wutsi.blog.mail.job

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.GetQueueUrlResult
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.ReceiveMessageResult
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ProcessSESComplaintsQueueJobTest {
    private val xemailService: XEmailService = mock()
    private val sqs: AmazonSQS = mock()
    private val lockManager: CronLockManager = mock()
    private val registry: CronJobRegistry = mock()
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val logger: KVLogger = DefaultKVLogger()
    private val job = ProcessSESComplaintsQueueJob(
        xemailService,
        objectMapper,
        logger,
        sqs,
        lockManager,
        registry,
        "ses-complaints-queue"
    )

    @BeforeEach
    fun setUp() {
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    fun queueName() {
        assertEquals("ses-complaints-queue", job.queueName())
    }

    @Test
    fun run() {
        // GIVEN
        val queueUrl = GetQueueUrlResult()
        queueUrl.queueUrl = "https://sqs.amazon.com/queue/" + UUID.randomUUID()
        doReturn(queueUrl).whenever(sqs).getQueueUrl("ses-complaints-queue")

        val result1 = ReceiveMessageResult().withMessages(
            createMessage("1", "handle-1"),
            createMessage("2", "handle-2"),
        )
        val result2 = ReceiveMessageResult()
        doReturn(result1).doReturn(result2).whenever(sqs).receiveMessage(any<ReceiveMessageRequest>())

        doReturn(true).whenever(xemailService).process(any())

        // WHEN
        job.run()

        // THEN
        verify(xemailService, times(2)).process(any())

//        val req = argumentCaptor<DeleteMessageRequest>()
//        verify(sqs, times(2)).deleteMessage(req.capture())
//        assertEquals(queueUrl.queueUrl, req.firstValue.queueUrl)
//        assertEquals("handle-1", req.firstValue.receiptHandle)
//        assertEquals(queueUrl.queueUrl, req.secondValue.queueUrl)
//        assertEquals("handle-2", req.secondValue.receiptHandle)
    }

    private fun createMessage(id: String, handle: String): Message {
        val message = Message()
        message.messageId = id
        message.receiptHandle = handle
        message.body = """
            {
                "Type": "Notification",
                "MessageId": "b7e1daba-9744-5f94-a25a-8700f6b7f13c",
                "TopicArn": "arn:aws:sns:us-east-1:828725992074:ses-bounces-topic",
                "Message": "{\"notificationType\":\"Bounce\",\"bounce\":{\"feedbackId\":\"0100018b8375d2c5-11a91301-cd10-411f-ae71-282f80290159-000000\",\"bounceType\":\"Permanent\",\"bounceSubType\":\"General\",\"bouncedRecipients\":[{\"emailAddress\":\"manuelatiadouanla@gmail.com\",\"action\":\"failed\",\"status\":\"5.1.1\",\"diagnosticCode\":\"smtp; 550-5.1.1 The email account that you tried to reach does not exist. Please try\n550-5.1.1 double-checking the recipient's email address for typos or\n550-5.1.1 unnecessary spaces. Learn more at\n550 5.1.1  https://support.google.com/mail/?p=NoSuchUser ea14-20020a05620a488e00b007683ee37758si254795qkb.86 - gsmtp\"}],\"timestamp\":\"2023-10-31T02:00:18.000Z\",\"remoteMtaIp\":\"142.251.163.27\",\"reportingMTA\":\"dns; a48-101.smtp-out.amazonses.com\"},\"mail\":{\"timestamp\":\"2023-10-31T02:00:18.505Z\",\"source\":\"no-reply@wutsi.com\",\"sourceArn\":\"arn:aws:ses:us-east-1:828725992074:identity/no-reply@wutsi.com\",\"sourceIp\":\"44.192.105.127\",\"callerIdentity\":\"ses-smtp-user.20200622-000521\",\"sendingAccountId\":\"828725992074\",\"messageId\":\"0100018b8375d149-b3a90120-37e5-4873-a60d-b64f70e677cb-000000\",\"destination\":[\"manuelatiadouanla@gmail.com\"],\"headersTruncated\":false,\"headers\":[{\"name\":\"Received\",\"value\":\"from 18634e5c-b7ce-4020-9372-0ded8d8958a3.prvt.dyno.rt.heroku.com (ec2-44-192-105-127.compute-1.amazonaws.com [44.192.105.127]) by email-smtp.amazonaws.com with SMTP (SimpleEmailService-d-C3FQ6JLI2) id I0DfNfvWKA2iKNTxczCL for manuelatiadouanla@gmail.com; Tue, 31 Oct 2023 02:00:18 +0000 (UTC)\"},{\"name\":\"Date\",\"value\":\"Tue, 31 Oct 2023 02:00:18 +0000 (UTC)\"},{\"name\":\"From\",\"value\":\"Visartculture Officiel <no-reply@wutsi.com>\"},{\"name\":\"To\",\"value\":\"manuelatiadouanla@gmail.com\"},{\"name\":\"Message-ID\",\"value\":\"<1758283775.43.1698717618482@18634e5c-b7ce-4020-9372-0ded8d8958a3.prvt.dyno.rt.heroku.com>\"},{\"name\":\"Subject\",\"value\":\"Les influenceurs Camerounais, à qui vendent-ils le rêve ? \"},{\"name\":\"MIME-Version\",\"value\":\"1.0\"},{\"name\":\"Content-Type\",\"value\":\"text/html;charset=UTF-8\"},{\"name\":\"Content-Transfer-Encoding\",\"value\":\"7bit\"}],\"commonHeaders\":{\"from\":[\"Visartculture Officiel <no-reply@wutsi.com>\"],\"date\":\"Tue, 31 Oct 2023 02:00:18 +0000 (UTC)\",\"to\":[\"manuelatiadouanla@gmail.com\"],\"messageId\":\"<1758283775.43.1698717618482@18634e5c-b7ce-4020-9372-0ded8d8958a3.prvt.dyno.rt.heroku.com>\",\"subject\":\"Les influenceurs Camerounais, à qui vendent-ils le rêve ? \"}}}"
            }
        """.trimIndent()
        return message
    }
}
