package com.wutsi.blog.user.it

import com.wutsi.blog.event.EventType.USER_ACTIVATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.job.UserActivationJob
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/user/ActivateUserJob.sql"])
class ActivateUserJobTest {
    @Autowired
    private lateinit var job: UserActivationJob

    @Autowired
    private lateinit var dao: UserRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun search() {
        job.run()

        assertFalse(dao.findById(1).get().active)
        assertFalse(dao.findById(2).get().active)
        assertTrue(dao.findById(3).get().active)
        assertFalse(dao.findById(4).get().active)
        assertTrue(dao.findById(5).get().active)

        val events = eventStore.events(StreamId.USER, type = USER_ACTIVATED_EVENT).sortedBy { it.entityId }
        assertEquals(1, events.size)

        assertEquals("3", events[0].entityId)
    }
}
