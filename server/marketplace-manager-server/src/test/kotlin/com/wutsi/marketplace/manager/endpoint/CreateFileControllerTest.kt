package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.CreateFileResponse
import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.dto.CreateFileRequest
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
class CreateFileControllerTest : AbstractSecuredControllerTest() {
    companion object {
        const val FILE_ID = 111L
    }

    @LocalServerPort
    val port: Int = 0

    private val request = CreateFileRequest(
        productId = PRODUCT_ID,
        url = "https://www.img.com/1.png",
        contentSize = 10000,
        contentType = "image/png",
        name = "Yo Man.png",
    )

    private fun url() = "http://localhost:$port/v1/files"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(CreateFileResponse(FILE_ID)).whenever(marketplaceAccessApi)
            .createFile(any())
    }

    @Test
    fun add() {
        // GIVEN
        val product = Fixtures.createProduct(
            id = PRODUCT_ID,
            storeId = STORE_ID,
            files = listOf(
                Fixtures.createFileSummary(1),
                Fixtures.createFileSummary(2),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val response =
            rest.postForEntity(url(), request, com.wutsi.marketplace.manager.dto.CreateFileResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(FILE_ID, response.body?.fileId)

        verify(marketplaceAccessApi).createFile(
            com.wutsi.marketplace.access.dto.CreateFileRequest(
                productId = request!!.productId,
                url = request!!.url,
                contentType = request!!.contentType,
                contentSize = request!!.contentSize,
                name = request!!.name,
            ),
        )

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun tooManyFiles() {
        // GIVEN
        val product = Fixtures.createProduct(
            id = PRODUCT_ID,
            storeId = STORE_ID,
            files = listOf(
                Fixtures.createFileSummary(1),
                Fixtures.createFileSummary(2),
                Fixtures.createFileSummary(3),
                Fixtures.createFileSummary(4),
                Fixtures.createFileSummary(5),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceAccessApi).getProduct(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, com.wutsi.marketplace.manager.dto.CreateFileResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_DIGITAL_DOWNLOAD_LIMIT_REACHED.urn, response.error.code)

        verify(marketplaceAccessApi, never()).createStore(any())
        verify(eventStream, never()).publish(any(), any())
    }
}
