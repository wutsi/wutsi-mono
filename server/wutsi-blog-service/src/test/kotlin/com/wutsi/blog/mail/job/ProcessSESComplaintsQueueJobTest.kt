package com.wutsi.blog.mail.job

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.GetQueueUrlResult
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.ReceiveMessageResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
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
            createMessage("1", "{}", "handle-1"),
            createMessage("2", "{}", "handle-2"),
        )
        val result2 = ReceiveMessageResult()
        doReturn(result1).doReturn(result2).whenever(sqs).receiveMessage(any<ReceiveMessageRequest>())

        doReturn(true).whenever(xemailService).process(any())

        // WHEN
        job.run()

        // THEN
        verify(xemailService, times(2)).process(any())

        val req = argumentCaptor<DeleteMessageRequest>()
        verify(sqs, times(2)).deleteMessage(req.capture())
        assertEquals(queueUrl.queueUrl, req.firstValue.queueUrl)
        assertEquals("handle-1", req.firstValue.receiptHandle)
        assertEquals(queueUrl.queueUrl, req.secondValue.queueUrl)
        assertEquals("handle-2", req.secondValue.receiptHandle)
    }

    private fun createMessage(id: String, body: String, handle: String): Message {
        val message = Message()
        message.messageId = id
        message.body = body
        message.receiptHandle = handle
        return message
    }
}
