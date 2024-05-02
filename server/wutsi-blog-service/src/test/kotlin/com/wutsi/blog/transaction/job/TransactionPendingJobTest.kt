package com.wutsi.blog.transaction.job

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/TransactionPendingJob.sql"])
class TransactionPendingJobTest {
    @Autowired
    private lateinit var job: TransactionPendingJob

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun execute() {
        // WHEN
        job.run()

        // THEN
        val payload = argumentCaptor<SubmitTransactionNotificationCommand>()
        verify(eventStream, times(2)).enqueue(eq(EventType.SUBMIT_TRANSACTION_NOTIFICATION_COMMAND), payload.capture())

        kotlin.test.assertEquals("101", payload.firstValue.transactionId)
        kotlin.test.assertEquals("103", payload.secondValue.transactionId)
    }
}