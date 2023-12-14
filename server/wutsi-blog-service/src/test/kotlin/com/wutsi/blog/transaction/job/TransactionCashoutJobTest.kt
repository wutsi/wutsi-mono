package com.wutsi.blog.transaction.job

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/TransactionCashoutJob.sql"])
class TransactionCashoutJobTest {
    @Autowired
    private lateinit var job: TransactionCashoutJob

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun execute() {
        // WHEN
        job.run()

        // THEN
        val payload = argumentCaptor<SubmitCashoutCommand>()
        verify(eventStream, times(2)).enqueue(eq(EventType.SUBMIT_CASHOUT_COMMAND), payload.capture())

        assertEquals("1", payload.firstValue.walletId)
        assertEquals(900L, payload.firstValue.amount)

        assertEquals("2", payload.secondValue.walletId)
        assertEquals(1900L, payload.secondValue.amount)
    }
}
