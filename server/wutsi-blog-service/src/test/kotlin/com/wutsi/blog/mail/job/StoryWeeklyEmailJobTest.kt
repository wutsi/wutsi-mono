package com.wutsi.blog.mail.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dao.StoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Sql(value = ["/db/clean.sql", "/db/mail/StoryWeeklyEmailJobTest.sql"])
class StoryWeeklyEmailJobTest : AbstractMailerTest() {
    @Autowired
    private lateinit var job: StoryWeeklyEmailJob

    @Autowired
    protected lateinit var storyDao: StoryRepository

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
        job.run()
        Thread.sleep(15000)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        print(messages[0])

        assertTrue(deliveredTo("tchbansi@hotmail.com", messages))
        assertTrue(deliveredTo("herve.tchepannou.ci@gmail.com", messages))
        assertTrue(deliveredTo("herve.tchepannou.sn@gmail.com", messages))
        assertFalse(deliveredTo("user-not-whitelisted@gmail.com", messages))
        assertFalse(deliveredTo("blackisted@gmail.com", messages))
    }
}
