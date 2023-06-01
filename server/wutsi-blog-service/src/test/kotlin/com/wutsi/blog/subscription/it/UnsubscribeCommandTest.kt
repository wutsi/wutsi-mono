package com.wutsi.blog.subscription.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.UNSUBSCRIBE_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dao.SubscriptionUserRepository
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/UnsubscribeCommand.sql"])
internal class UnsubscribeCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var subscriptionDao: SubscriptionRepository

    @Autowired
    private lateinit var userDao: SubscriptionUserRepository

    private fun unsubscribe(userId: Long, subscriberId: Long) {
        eventHandler.handle(
            Event(
                type = UNSUBSCRIBE_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    UnsubscribeCommand(
                        userId = userId,
                        subscriberId = subscriberId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun unsubscribe() {
        // WHEN
        val now = Date()
        unsubscribe(1, 10)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(1, 10)
        assertNull(subscription)

        val user = userDao.findById(1)
        assertEquals(1, user.get().count)
    }

    @Test
    fun subscribeLast() {
        // WHEN
        val now = Date()
        unsubscribe(3, 10)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(2, 10)
        assertNull(subscription)

        val user = userDao.findById(3)
        assertEquals(0, user.get().count)
    }

    @Test
    fun unsubscribeNotNotSubscribed() {
        // WHEN
        val now = Date()
        unsubscribe(10, 11)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(10, 11)
        assertNull(subscription)
    }
}
