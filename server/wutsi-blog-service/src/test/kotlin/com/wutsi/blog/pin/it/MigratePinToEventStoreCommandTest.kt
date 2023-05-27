package com.wutsi.blog.pin.it

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/pin/MigratePinToEventStoreCommand.sql"])
class MigratePinToEventStoreCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun migrate() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/pins/commands/migrate-to-event-stream",
            Any::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(1000)
        val payload = argumentCaptor<PinStoryCommand>()
        verify(eventStream, times(2)).enqueue(eq(PIN_STORY_COMMAND), payload.capture())

        assertEquals(20L, payload.firstValue.storyId)
        assertEquals(30L, payload.secondValue.storyId)
    }
}
