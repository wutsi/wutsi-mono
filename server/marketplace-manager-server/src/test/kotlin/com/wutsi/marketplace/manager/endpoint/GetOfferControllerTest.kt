package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.GetOfferResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetOfferControllerTest : AbstractControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        val offer = Fixtures.createOffer(1)
        doReturn(com.wutsi.marketplace.access.dto.GetOfferResponse(offer)).whenever(marketplaceAccessApi).getOffer(1L)

        val response = rest.getForEntity(url(1L), GetOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/offers/$id"
}
