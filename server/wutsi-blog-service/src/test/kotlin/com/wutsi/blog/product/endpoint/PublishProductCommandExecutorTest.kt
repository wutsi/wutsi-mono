package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.PublishProductCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/PublishProductCommand.sql"])
class PublishProductCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    @Test
    fun execute() {
        val request = PublishProductCommand(
            productId = 100
        )
        val response = rest.postForEntity("/v1/products/commands/publish", request, Any::class.java)
        assertEquals(200, response.statusCode.value())

        val product = dao.findById(100L).get()
        assertEquals(ProductStatus.PUBLISHED, product.status)

        Thread.sleep(15000)
        val store = storeDao.findById(product.store.id!!).get()
        assertEquals(1, store.publishProductCount)
        assertEquals(3, store.productCount)
    }
}
