package com.wutsi.blog.mail.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.TimeZone
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Sql(value = ["/db/clean.sql", "/db/mail/OrderAbandonedHourlyJob.sql"])
class OrderAbandonedHourlyJobTest : AbstractMailerTest() {
    @Autowired
    private lateinit var job: OrderAbandonedHourlyJob

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    protected lateinit var storyDao: StoryRepository

    @MockBean
    protected lateinit var clock: Clock

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        val date = fmt.parse("2020-10-04 08:45:00")
        doReturn(date.time).whenever(clock).millis()
    }

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        print(messages[0])

        assertTrue(deliveredTo("herve.tchepannou@gmail.com", messages))
        assertFalse(deliveredTo("tchbansi@hotmail.com", messages)) // Not abandonned
        assertFalse(deliveredTo("herve.tchepannou.ci@gmail.com", messages)) // Already sent
        assertFalse(deliveredTo("herve.tchepannou.sn@gmail.com", messages)) // Previous hour window

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = "100",
            userId = "2",
            type = EventType.TRANSACTION_ABANDONED_HOURLY_EMAIL_SENT_EVENT,
        )
        assertTrue(events.isNotEmpty())
    }
}
