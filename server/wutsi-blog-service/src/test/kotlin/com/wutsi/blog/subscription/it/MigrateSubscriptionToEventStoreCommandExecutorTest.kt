package com.wutsi.blog.subscription.it

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.service.SubscriptionService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/MigrateSubscriptionToEventStoreCommand.sql"])
class MigrateSubscriptionToEventStoreCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var service: SubscriptionService

    @Test
    fun migrate() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/subscriptions/commands/migrate-to-event-stream",
            Any::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(1000)
        val payload = argumentCaptor<SubscribeCommand>()
        verify(service, times(9)).subscribe(payload.capture())
    }
}
