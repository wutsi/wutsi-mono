package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.MeetingProviderType
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dto.GetCategoryResponse
import com.wutsi.marketplace.access.dto.GetProductResponse
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetProductController.sql"])
class GetProductControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun physicalProduct() {
        val response = rest.getForEntity(url(100), GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.product
        assertEquals(1L, product.store.id)
        assertEquals(11L, product.store.accountId)
        assertEquals("XAF", product.store.currency)
        assertEquals("TV", product.title)
        assertEquals("summary of TV", product.summary)
        assertEquals("description of TV", product.description)
        assertEquals(ProductStatus.PUBLISHED.name, product.status)
        assertEquals(150000L, product.price)
        assertEquals(10, product.quantity)
        assertFalse(product.outOfStock)
        assertEquals("XAF", product.currency)
        assertEquals(ProductType.PHYSICAL_PRODUCT.name, product.type)
        assertNull(product.event)
        assertTrue(product.files.isEmpty())

        assertEquals(1110L, product.category?.id)
        assertEquals("Computers", product.category?.title)
        assertEquals(1100L, product.category?.parentId)

        assertEquals(101, product.thumbnail?.id)
        assertEquals("https://www.img.com/101.png", product.thumbnail?.url)

        assertEquals(2, product.pictures.size)
        assertEquals(101, product.pictures[0].id)
        assertEquals("https://www.img.com/101.png", product.pictures[0].url)

        assertEquals(102, product.pictures[1].id)
        assertEquals("https://www.img.com/102.png", product.pictures[1].url)
    }

    @Test
    fun event() {
        val response = rest.getForEntity(url(200), GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.product
        assertEquals(1L, product.store.id)
        assertEquals(11L, product.store.accountId)
        assertEquals("XAF", product.store.currency)
        assertEquals("TV", product.title)
        assertEquals("summary of TV", product.summary)
        assertEquals("description of TV", product.description)
        assertEquals(ProductStatus.PUBLISHED.name, product.status)
        assertEquals(150000L, product.price)
        assertEquals(10, product.quantity)
        assertFalse(product.outOfStock)
        assertEquals("XAF", product.currency)
        assertEquals(ProductType.EVENT.name, product.type)
        assertEquals(100, product.totalOrders)
        assertEquals(150, product.totalUnits)
        assertEquals(1500000, product.totalSales)
        assertEquals(2000000, product.totalViews)

        assertEquals("1234567890", product.event?.meetingId)
        assertEquals("123456", product.event?.meetingPassword)
        assertEquals(MeetingProviderType.ZOOM.name, product.event?.meetingProvider?.type)
        assertEquals("https://us04web.zoom.us/meeting/1234567890", product.event?.meetingJoinUrl)
        assertEquals(true, product.event?.online)

        assertTrue(product.files.isEmpty())

        assertEquals(1110L, product.category?.id)
        assertEquals("Computers", product.category?.title)
        assertEquals(1100L, product.category?.parentId)
    }

    @Test
    fun file() {
        val response = rest.getForEntity(url(300), GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.product
        assertEquals(1L, product.store.id)
        assertEquals(11L, product.store.accountId)
        assertEquals("XAF", product.store.currency)
        assertEquals("TV", product.title)
        assertEquals("summary of TV", product.summary)
        assertEquals("description of TV", product.description)
        assertEquals(ProductStatus.PUBLISHED.name, product.status)
        assertEquals("/p/300/tv", product.url)
        assertEquals(150000L, product.price)
        assertNull(product.quantity)
        assertFalse(product.outOfStock)
        assertEquals("XAF", product.currency)
        assertEquals(ProductType.DIGITAL_DOWNLOAD.name, product.type)
        assertNull(product.event)

        assertEquals(2, product.files.size)
        assertEquals(301, product.files[0].id)
        assertEquals("File-301", product.files[0].name)
        assertEquals("https://www.img.com/301.png", product.files[0].url)
        assertEquals("image/png", product.files[0].contentType)
        assertEquals(10240, product.files[0].contentSize)

        assertEquals(302, product.files[1].id)
        assertEquals("File-302", product.files[1].name)
        assertEquals("https://www.img.com/302.pdf", product.files[1].url)
        assertEquals("application/pdf", product.files[1].contentType)
        assertEquals(35000, product.files[1].contentSize)

        assertEquals(1110L, product.category?.id)
        assertEquals("Computers", product.category?.title)
        assertEquals(1100L, product.category?.parentId)
    }

    @Test
    fun outOfScope() {
        val response = rest.getForEntity(url(400), GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.product
        assertEquals(0, product.quantity)
        assertTrue(product.outOfStock)
    }

    @Test
    fun notFound() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(99999), GetCategoryResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun deleted() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(199), GetCategoryResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_DELETED.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/products/$id"
}
