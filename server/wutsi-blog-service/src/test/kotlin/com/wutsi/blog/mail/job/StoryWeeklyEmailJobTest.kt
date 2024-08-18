package com.wutsi.blog.mail.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/clean.sql", "/db/mail/StoryWeeklyEmailJobTest.sql"])
class StoryWeeklyEmailJobTest : AbstractMailerTest() {
    @Autowired
    private lateinit var job: StoryWeeklyEmailJob

    @Autowired
    protected lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var userDao: UserRepository

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
        assertEmailSent("tchbansi@hotmail.com")

        assertTrue(deliveredTo("herve.tchepannou.ci@gmail.com", messages))
        assertEmailSent("herve.tchepannou.ci@gmail.com")

        assertTrue(deliveredTo("herve.tchepannou.sn@gmail.com", messages))
        assertEmailSent("herve.tchepannou.sn@gmail.com")

        assertFalse(deliveredTo("user-not-whitelisted@gmail.com", messages))
        assertEmailNotSent("user-not-whitelisted@gmail.com")

        assertFalse(deliveredTo("blackisted@gmail.com", messages))
        assertEmailNotSent("blackisted@gmail.com")
    }

    private fun assertEmailSent(email: String) {
        val user = userDao.findByEmailIgnoreCase(email).getOrNull()
        assertNotNull(user?.lastWeeklyEmailSentDateTime)
    }

    private fun assertEmailNotSent(email: String) {
        val user = userDao.findByEmailIgnoreCase(email).getOrNull()
        assertNull(user?.lastWeeklyEmailSentDateTime)
    }
}
