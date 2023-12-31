package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.dto.UpdateStoreDiscountsCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/UpdateStoreDiscountsCommand.sql"])
class UpdateStoreDiscountsCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: StoreRepository

    @Test
    fun update() {
        val request = UpdateStoreDiscountsCommand(
            storeId = "1",
            subscriberDiscount = 15,
            firstPurchaseDiscount = 20,
            nextPurchaseDiscount = 30,
            nextPurchaseDiscountDays = 14,
        )
        val result = rest.postForEntity("/v1/stores/commands/update-discounts", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val store = dao.findById(request.storeId).get()
        assertEquals(request.subscriberDiscount, store.subscriberDiscount)
        assertEquals(request.firstPurchaseDiscount, store.firstPurchaseDiscount)
        assertEquals(request.nextPurchaseDiscount, store.nextPurchaseDiscount)
        assertEquals(request.nextPurchaseDiscountDays, store.nextPurchaseDiscountDays)
    }
}
