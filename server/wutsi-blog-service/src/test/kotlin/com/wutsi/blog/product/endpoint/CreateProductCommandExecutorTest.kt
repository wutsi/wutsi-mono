package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.dto.CreateProductCommand
import com.wutsi.blog.product.dto.CreateProductResponse
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/CreateProductCommand.sql"])
class CreateProductCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: ProductRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    @Test
    fun execute() {
        val request = CreateProductCommand(
            externalId = "111",
            title = "product 111",
            description = "This is an example of product",
            categoryId = 1000,
            price = 15000,
            type = ProductType.EBOOK,
            storeId = "1",
            available = true
        )

        val response = rest.postForEntity("/v1/products/commands/create", request, CreateProductResponse::class.java)
        assertEquals(200, response.statusCode.value())

        val productId = response.body!!.productId
        val product = dao.findById(productId).get()

        assertEquals(request.externalId, product.externalId)
        assertEquals(request.categoryId, product.category?.id)
        assertEquals(request.price, product.price)
        assertEquals(request.type, product.type)
        assertEquals(request.storeId, product.store.id)
        assertEquals(request.available, product.available)
        assertEquals(request.title, product.title)
        assertEquals(request.description, product.description)
        assertEquals(ProductStatus.PUBLISHED, product.status)

        Thread.sleep(15000)
        val store = storeDao.findById(product.store.id!!).get()
        assertEquals(3, store.publishProductCount)
        assertEquals(4, store.productCount)
    }
}
