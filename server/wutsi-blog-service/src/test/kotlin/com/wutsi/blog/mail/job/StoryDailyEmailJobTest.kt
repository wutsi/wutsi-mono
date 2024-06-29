package com.wutsi.blog.mail.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.service.sender.story.DailyMailSender
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/clean.sql", "/db/mail/StoryDailyEmailJobTest.sql"])
class StoryDailyEmailJobTest : AbstractMailerTest() {
    @Autowired
    private lateinit var job: StoryDailyEmailJob

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    protected lateinit var storyDao: StoryRepository

    @Autowired
    protected lateinit var userDao: UserRepository

    @Autowired
    protected lateinit var subscriptionDao: SubscriptionRepository

    @MockBean
    protected lateinit var clock: Clock

    @Value("\${wutsi.application.website-url}")
    private lateinit var webappUrl: String

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

        assertTrue(hasHeader(DailyMailSender.HEADER_STORY_ID, "10", messages[0]))
        assertTrue(
            hasHeader(
                DailyMailSender.HEADER_UNSUBSCRIBE,
                "<$webappUrl/@/ray.sponsible/unsubscribe?email=herve.tchepannou@gmail.com>",
                messages[0]
            )
        )

        assertTrue(deliveredTo("herve.tchepannou@gmail.com", messages))
        assertFalse(deliveredTo("user-not-whitelisted@gmail.com", messages))
        assertFalse(deliveredTo("blacklisted@gmail.com", messages))
        assertFalse(deliveredTo("tchbansi@hotmail.com", messages))
        assertTrue(deliveredTo("herve.tchepannou.ci@gmail.com", messages))

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = "10",
            type = EventType.STORY_DAILY_EMAIL_SENT_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val story = storyDao.findById(10L).get()
        assertEquals(3L, story.recipientCount)

        assertSubscriptionSentDate("herve.tchepannou@gmail.com", 1L)
    }

    private fun assertSubscriptionSentDate(email: String, userId: Long) {
        val subscriber = userDao.findByEmailIgnoreCase(email).get()
        val subscription = subscriptionDao.findByUserIdAndSubscriberId(userId, subscriber.id!!)
        assertNotNull(subscription?.lastEmailSentDateTime)
    }
}
