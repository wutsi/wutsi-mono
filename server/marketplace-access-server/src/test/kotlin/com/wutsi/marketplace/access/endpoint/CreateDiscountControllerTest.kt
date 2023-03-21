package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.DiscountType
import com.wutsi.marketplace.access.dao.DiscountRepository
import com.wutsi.marketplace.access.dto.CreateDiscountRequest
import com.wutsi.marketplace.access.dto.CreateDiscountResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateDiscountController.sql"])
class CreateDiscountControllerTest {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: DiscountRepository

    private val rest = RestTemplate()

    @Test
    fun create() {
        // GIVEN
        val request = CreateDiscountRequest(
            name = "FIN25",
            starts = OffsetDateTime.now(),
            ends = OffsetDateTime.now().plusDays(15),
            rate = 15,
            allProducts = true,
            storeId = 1L,
            type = DiscountType.SALES.name,
        )
        val response = rest.postForEntity(url(), request, CreateDiscountResponse::class.java)

        // THEN
        val discountId = response.body!!.discountId
        val discount = dao.findById(discountId).get()
        assertEquals(request.name, discount.name)
        assertEquals(request.starts!!.toInstant().toEpochMilli() / 10000, discount.starts!!.time / 10000)
        assertEquals(request.ends!!.toInstant().toEpochMilli() / 10000, discount.ends!!.time / 10000)
        assertEquals(request.allProducts, discount.allProducts)
        assertEquals(request.rate, discount.rate)
        assertEquals(DiscountType.SALES, discount.type)
        assertFalse(discount.isDeleted)
        assertNull(discount.deleted)
    }

    private fun url() = "http://localhost:$port/v1/discounts"
}
