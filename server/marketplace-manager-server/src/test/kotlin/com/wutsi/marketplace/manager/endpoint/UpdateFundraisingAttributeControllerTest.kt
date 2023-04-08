package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateFundraisingAttributeControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    private fun url(id: Long) = "http://localhost:$port/v1/fundraisings/$id/attributes"

    @Test
    public fun invoke() {
        // WHEN
        val response = rest.postForEntity(
            url(100),
            com.wutsi.marketplace.manager.dto.UpdateFundraisingAttributeRequest("foo", "bar"),
            Any::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).updateFundraisingAttribute(
            100,
            UpdateFundraisingAttributeRequest("foo", "bar"),
        )
    }
}
