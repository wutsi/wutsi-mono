package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetProductResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/GetProductQuery.sql"])
class GetProductQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/products/1", GetProductResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val product = result.body!!.product
        assertEquals("1", product.storeId)
        assertEquals("100", product.externalId)
        assertEquals("product 1", product.title)
        assertEquals("description 1", product.description)
        assertEquals(1000, product.price)
        assertEquals("XAF", product.currency)
        assertEquals(11L, product.orderCount)
        assertEquals(11000L, product.totalSales)
        assertEquals("https://picsum/100/100", product.imageUrl)
        assertEquals("https://file.com/file.pdf", product.fileUrl)
        assertEquals("application/pdf", product.fileContentType)
        assertEquals(1000, product.fileContentLength)

        assertEquals(1001, product.category?.id)
        assertEquals("Autobiography", product.category?.title)
        assertEquals("Literature > Autobiography", product.category?.longTitle)
        assertEquals(1, product.category?.level)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/products/999", GetProductResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }
}
