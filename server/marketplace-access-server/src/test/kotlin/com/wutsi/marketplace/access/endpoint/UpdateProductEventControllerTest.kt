package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dto.UpdateProductEventRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateProductEventController.sql"])
public class UpdateProductEventControllerTest {
    companion object {
        const val PRODUCT_ID = 100L
    }

    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: ProductRepository

    @Test
    public fun invoke() {
        // WHEN
        val request = UpdateProductEventRequest(
            meetingProviderId = 1000L,
            meetingId = "1111111",
            meetingPassword = "12345",
            starts = OffsetDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC),
            ends = OffsetDateTime.of(2020, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
            online = true,
        )
        val response = rest.postForEntity(url(100), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val product = dao.findById(100).get()
        assertEquals(request.meetingProviderId, product.eventMeetingProvider?.id)
        assertEquals(request.meetingId, product.eventMeetingId)
        assertEquals(request.meetingPassword, product.eventMeetingPassword)
        assertEquals(Date(request.starts!!.toInstant().toEpochMilli()), product.eventStarts)
        assertEquals(Date(request.ends!!.toInstant().toEpochMilli()), product.eventEnds)
        assertEquals(request.online, product.eventOnline)
    }

    private fun url(productId: Long = 100L) =
        "http://localhost:$port/v1/products/$productId/event"
}
