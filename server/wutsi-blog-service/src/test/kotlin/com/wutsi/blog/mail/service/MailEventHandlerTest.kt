package com.wutsi.blog.mail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.EmailOpenedEvent
import com.wutsi.blog.mail.dto.EmailType
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/mail/MailEventHandler.sql"])
internal class MailEventHandlerTest {
    @Autowired
    private lateinit var handler: MailEventHandler

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var subscriptionrDao: SubscriptionRepository

    @Test
    fun weeklyDigestOpened() {
        handler.handle(
            Event(
                type = EventType.EMAIL_OPENED_EVENT,
                payload = ObjectMapper().writeValueAsString(
                    EmailOpenedEvent(
                        type = EmailType.WEEKLY_DIGEST,
                        userId = 4L
                    )
                )
            )
        )

        val user = userDao.findById(4L).getOrNull()
        assertNotNull(user?.lastWeeklyEmailOpenedDateTime)
    }

    @Test
    fun dailyEmailOpened() {
        handler.handle(
            Event(
                type = EventType.EMAIL_OPENED_EVENT,
                payload = ObjectMapper().writeValueAsString(
                    EmailOpenedEvent(
                        type = EmailType.DAILY_EMAIL,
                        userId = 4L,
                        storyId = 10L,
                    )
                )
            )
        )

        val subscription = subscriptionrDao.findByUserIdAndSubscriberId(1L, 4L)
        assertNotNull(subscription?.lastEmailOpenedDateTime)
    }
}
