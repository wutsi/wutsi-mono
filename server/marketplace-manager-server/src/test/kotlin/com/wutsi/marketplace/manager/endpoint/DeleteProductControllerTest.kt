package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteProductControllerTest : AbstractProductControllerTest<Void>() {
    override fun url() = "http://localhost:$port/v1/products/$PRODUCT_ID"

    override fun createRequest(): Void? = null

    override fun submit() {
        rest.delete(url())
    }

    @Test
    fun delete() {
        submit()

        verify(marketplaceAccessApi).deleteProduct(PRODUCT_ID)
    }
}
