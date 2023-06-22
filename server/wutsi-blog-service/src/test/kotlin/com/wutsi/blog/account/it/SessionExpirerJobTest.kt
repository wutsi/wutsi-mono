package com.wutsi.blog.account.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.account.job.SessionExpirerJob
import com.wutsi.blog.event.EventType.LOGOUT_USER_COMMAND
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/SessionExpirerJob.sql"])
class SessionExpirerJobTest {
    @Autowired
    private lateinit var job: SessionExpirerJob

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun logout() {
        job.run()

        verify(eventStream, times(2)).publish(any(), any())
        eventStream.enqueue(LOGOUT_USER_COMMAND, LogoutUserCommand("101"))
        eventStream.enqueue(LOGOUT_USER_COMMAND, LogoutUserCommand("102"))
    }
}
