package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateProductEventControllerTest : AbstractProductControllerTest<UpdateProductEventRequest>() {
    override fun url() = "http://localhost:$port/v1/products/event"

    override fun createRequest() = UpdateProductEventRequest(
        productId = PRODUCT_ID,
        meetingProviderId = 1000,
        starts = OffsetDateTime.of(2020, 10, 1, 12, 0, 0, 0, ZoneOffset.UTC),
        ends = OffsetDateTime.of(2020, 10, 1, 12, 3, 0, 0, ZoneOffset.UTC),
        meetingId = "1234567890",
        meetingPassword = "123456",
        online = true,
    )

    @Test
    public fun invoke() {
        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<UpdateProductAttributeRequest>()
        verify(marketplaceAccessApi).updateProductEvent(
            id = request!!.productId,
            request = com.wutsi.marketplace.access.dto.UpdateProductEventRequest(
                meetingProviderId = request!!.meetingProviderId,
                meetingPassword = request!!.meetingPassword,
                meetingId = request!!.meetingId,
                ends = request!!.ends,
                starts = request!!.starts,
                online = request!!.online,
            ),
        )

        verify(eventStream, never()).publish(any(), any())
    }
}
