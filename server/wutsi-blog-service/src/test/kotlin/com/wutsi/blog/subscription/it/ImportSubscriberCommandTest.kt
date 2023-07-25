package com.wutsi.blog.subscription.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.IMPORT_SUBSCRIBER_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/ImportSubscriberCommand.sql"])
internal class ImportSubscriberCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var subscriptionDao: SubscriptionRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun import() {
        // GIVEN
        val content = ByteArrayInputStream(
            """
                id,name,email
                1,Roger Milla,roger-milla@gmail.com
                2,Samuel Etooo,samuel-etoo@gmail.com
                3,Kounde Emanuel,kunde@gmail.com
                4,No email
            """.trimIndent().toByteArray(),
        )
        val url = storage.store("/mailing-list/1/email.csv", content, "text/csv")

        // WHEN
        eventHandler.handle(
            Event(
                type = IMPORT_SUBSCRIBER_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    ImportSubscriberCommand(
                        userId = 1,
                        url = url.toString(),
                    ),
                ),
            ),
        )

        val events = eventStore.events(
            streamId = StreamId.SUBSCRIPTION,
            entityId = "4",
            type = EventType.IMPORT_SUBSCRIBER_COMMAND,
        )
        assertTrue(events.isEmpty())

        Thread.sleep(30000L)

        assertSubscriber(1, "roger-milla@gmail.com")
        assertSubscriber(1, "samuel-etoo@gmail.com")
        assertSubscriber(1, "kunde@gmail.com")
    }

    private fun assertSubscriber(userId: Long, email: String) {
        val subscriber = userDao.findByEmailIgnoreCase(email)
        assertTrue(subscriber.isPresent)
        assertNotNull(subscriptionDao.findByUserIdAndSubscriberId(userId, subscriber.get().id!!))
    }
}
