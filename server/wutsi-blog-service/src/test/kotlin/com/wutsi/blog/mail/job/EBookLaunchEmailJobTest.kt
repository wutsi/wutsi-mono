package com.wutsi.blog.mail.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Sql(value = ["/db/clean.sql", "/db/mail/EBookLaunchEmailJob.sql"])
class EBookLaunchEmailJobTest : AbstractMailerTest() {
    @Autowired
    private lateinit var job: EBookLaunchEmailJob

    @Autowired
    private lateinit var eventStore: EventStore

    @MockBean
    protected lateinit var clock: Clock

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val date = SimpleDateFormat("yyyy-MM-dd").parse("2020-02-20")
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
        assertFalse(deliveredTo("user-not-whitelisted@gmail.com", messages))
        assertFalse(deliveredTo("blacklisted@gmail.com", messages))

        val events = eventStore.events(
            streamId = StreamId.PRODUCT,
            entityId = "101",
            type = EventType.PRODUCT_EBOOK_LAUNCH_EMAIL_SENT_EVENT,
        )
        assertTrue(events.isNotEmpty())
    }
}