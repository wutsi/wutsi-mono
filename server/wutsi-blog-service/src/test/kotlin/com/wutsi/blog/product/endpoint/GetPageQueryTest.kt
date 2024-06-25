package com.wutsi.blog.product.endpoint

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dto.GetPageResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/GetPageQuery.sql"])
class GetPageQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/products/1/pages/2", GetPageResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val product = result.body!!.page
        assertEquals(2, product.number)
        assertEquals(1L, product.productId)
        assertEquals("image/png", product.contentType)
        assertEquals("https://www.img.com/2.png", product.contentUrl)
    }

    @Test
    fun productNotFound() {
        val result = rest.getForEntity("/v1/products/999/pages/1", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun overflow() {
        val result = rest.getForEntity("/v1/products/1/pages/1000", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.PAGE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun underflow() {
        val result = rest.getForEntity("/v1/products/1/pages/0", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.PAGE_NOT_FOUND, result.body?.error?.code)
    }
}