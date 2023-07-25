package com.wutsi.blog.subscription.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/SubscribeCommand.sql"])
internal class SubscribeCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var subscriptionDao: SubscriptionRepository

    @Autowired
    private lateinit var userDao: UserRepository

    private fun subscribe(userId: Long, subscriberId: Long, email: String? = null) {
        eventHandler.handle(
            Event(
                type = SUBSCRIBE_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    SubscribeCommand(
                        userId = userId,
                        subscriberId = subscriberId,
                        email = email,
                    ),
                ),
            ),
        )
    }

    @Test
    fun subscribe() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        subscribe(1, 2)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(1, 2)
        assertNotNull(subscription)
        assertTrue(subscription.timestamp.after(now))

        val user = userDao.findById(1)
        assertEquals(2, user.get().subscriberCount)
    }

    @Test
    fun subscribeFirst() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        subscribe(2, 3)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(2, 3)
        assertNotNull(subscription)
        assertTrue(subscription.timestamp.after(now))

        val user = userDao.findById(2)
        assertEquals(1, user.get().subscriberCount)
    }

    @Test
    fun subscribeSelf() {
        // WHEN
        subscribe(1, 1)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(1, 1)
        assertNull(subscription)
    }

    @Test
    fun subscribeAgain() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        subscribe(3, 2)

        Thread.sleep(10000L)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(3, 2)
        assertNotNull(subscription)
        assertTrue(subscription.timestamp.before(now))

        val user = userDao.findById(3)
        assertEquals(1, user.get().subscriberCount)
    }

    @Test
    fun subscribeEmail() {
        // WHEN
        val email = "email-subscriber@gmail.com"
        val now = Date()
        Thread.sleep(1000)
        subscribe(4, -1, email)

        Thread.sleep(10000L)

        val subscriber = userDao.findByEmailIgnoreCase(email).get()
        assertTrue(subscriber.creationDateTime.after(now))
        assertEquals(email, subscriber.email)
        assertEquals("email-subscriber", subscriber.name)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(4, subscriber.id!!)
        assertNotNull(subscription)
        assertTrue(subscription.timestamp.after(now))

        val user = userDao.findById(4)
        assertEquals(1, user.get().subscriberCount)
    }
}
