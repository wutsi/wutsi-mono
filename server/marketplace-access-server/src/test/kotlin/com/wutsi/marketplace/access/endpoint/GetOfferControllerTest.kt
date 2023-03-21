package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.MeetingProviderType
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.marketplace.access.dto.GetOfferResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetOfferController.sql"])
public class GetOfferControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun productWithDiscount() {
        val response = rest.getForEntity(url(100), GetOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.offer.product
        assertEquals(1L, product.store.id)
        assertEquals(11L, product.store.accountId)
        assertEquals("XAF", product.store.currency)
        assertEquals("TV", product.title)
        assertEquals("summary of TV", product.summary)
        assertEquals("description of TV", product.description)
        assertEquals(ProductStatus.PUBLISHED.name, product.status)
        assertEquals(150000L, product.price)
        assertEquals(10, product.quantity)
        assertEquals("XAF", product.currency)
        assertEquals(ProductType.PHYSICAL_PRODUCT.name, product.type)
        Assertions.assertNull(product.event)
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

        val price = response.body!!.offer.price
        assertEquals(100L, price.productId)
        assertEquals(112500L, price.price)
        assertEquals(150000L, price.referencePrice)
        assertEquals(101L, price.discountId)
        assertEquals(37500L, price.savings)
        assertEquals(25, price.savingsPercentage)
        assertNotNull(price.expires)
    }

    @Test
    public fun productWithoutDiscount() {
        val response = rest.getForEntity(url(200), GetOfferResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val product = response.body!!.offer.product
        assertEquals(2L, product.store.id)
        assertEquals(22L, product.store.accountId)
        assertEquals("XAF", product.store.currency)
        assertEquals("TV", product.title)
        assertEquals("summary of TV", product.summary)
        assertEquals("description of TV", product.description)
        assertEquals(ProductStatus.PUBLISHED.name, product.status)
        assertEquals(150000L, product.price)
        assertEquals(10, product.quantity)
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

        assertEquals(null, product.category)

        val price = response.body!!.offer.price
        assertEquals(200L, price.productId)
        assertEquals(150000L, price.price)
        assertNull(price.referencePrice)
        assertNull(price.discountId)
        assertEquals(0, price.savings)
        assertEquals(0, price.savingsPercentage)
        assertNull(price.expires)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/offers/$id"
}
