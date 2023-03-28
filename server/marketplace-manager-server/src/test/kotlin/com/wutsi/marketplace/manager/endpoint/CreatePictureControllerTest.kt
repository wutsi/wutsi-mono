package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreatePictureControllerTest : AbstractSecuredControllerTest() {
    companion object {
        const val PICTURE_ID = 111L
    }

    @LocalServerPort
    val port: Int = 0

    private val request = CreatePictureRequest(
        productId = PRODUCT_ID,
        url = "https://www.img.com/1.png",
    )

    private fun url() = "http://localhost:$port/v1/pictures"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(com.wutsi.marketplace.access.dto.CreatePictureResponse(PICTURE_ID)).whenever(marketplaceAccessApi)
            .createPicture(any())
    }

    @Test
    fun add() {
        // GIVEN
        val product = Fixtures.createProduct(
            id = PRODUCT_ID,
            storeId = STORE_ID,
            pictures = listOf(
                Fixtures.createPictureSummary(),
                Fixtures.createPictureSummary(),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val response =
            rest.postForEntity(url(), request, com.wutsi.marketplace.manager.dto.CreatePictureResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(PICTURE_ID, response.body?.pictureId)

        verify(marketplaceAccessApi).createPicture(
            com.wutsi.marketplace.access.dto.CreatePictureRequest(
                productId = request.productId,
                url = request.url,
            ),
        )

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun tooManyPictures() {
        // GIVEN
        val product = Fixtures.createProduct(
            id = PRODUCT_ID,
            storeId = STORE_ID,
            pictures = listOf(
                Fixtures.createPictureSummary(),
                Fixtures.createPictureSummary(),
                Fixtures.createPictureSummary(),
                Fixtures.createPictureSummary(),
                Fixtures.createPictureSummary(),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, com.wutsi.marketplace.manager.dto.CreatePictureResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PICTURE_LIMIT_REACHED.urn, response.error.code)

        verify(marketplaceAccessApi, never()).createStore(any())
        verify(eventStream, never()).publish(any(), any())
    }
}
