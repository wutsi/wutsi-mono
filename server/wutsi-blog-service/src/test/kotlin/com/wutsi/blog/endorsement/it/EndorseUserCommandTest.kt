package com.wutsi.blog.endorsement.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.endorsement.dao.EndorsementRepository
import com.wutsi.blog.endorsement.dto.EndorseUserCommand
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.ENDORSE_USER_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
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
@Sql(value = ["/db/clean.sql", "/db/endorsement/EndorseUserCommand.sql"])
internal class EndorseUserCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var dao: EndorsementRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var eventStore: EventStore

    private fun endorse(
        userId: Long,
        endorserId: Long,
        blurb: String? = null,
    ) {
        eventHandler.handle(
            Event(
                type = ENDORSE_USER_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    EndorseUserCommand(
                        userId = userId,
                        endorserId = endorserId,
                        blurb = blurb,
                    ),
                ),
            ),
        )
    }

    @Test
    fun endorse() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        endorse(1, 2, blurb = "Hello worled")

        Thread.sleep(10000L)

        val events = eventStore.events(
            streamId = StreamId.ENDORSEMENT,
            entityId = "1",
            userId = "2",
            type = EventType.USER_ENDORSED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val endorsement = dao.findByUserIdAndEndorserId(1, 2)
        assertNotNull(endorsement)
        assertTrue(endorsement.creationDateTime.after(now))

        val user = userDao.findById(1)
        assertEquals(2, user.get().endorserCount)
    }

    @Test
    fun subscribeSelf() {
        // WHEN
        endorse(1, 1)

        Thread.sleep(10000L)

        val endorsement = dao.findByUserIdAndEndorserId(1, 1)
        assertNull(endorsement)
    }

    @Test
    fun subscribeAgain() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        endorse(1, 3)

        Thread.sleep(10000L)

        val endorsement = dao.findByUserIdAndEndorserId(1, 3)
        assertNotNull(endorsement)
        assertTrue(endorsement.creationDateTime.before(now))
    }
}
