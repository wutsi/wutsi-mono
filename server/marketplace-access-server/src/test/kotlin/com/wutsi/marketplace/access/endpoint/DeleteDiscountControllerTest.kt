package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dao.DiscountRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import java.util.Date
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteDiscountController.sql"])
class DeleteDiscountControllerTest {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: DiscountRepository

    private val rest = RestTemplate()

    @Test
    fun delete() {
        // WHEN
        rest.delete(url(100L))

        // THEN
        val discount = dao.findById(100L).get()
        assertTrue(discount.isDeleted)
        assertNotNull(discount.deleted)
    }

    @Test
    fun alreadyDeleted() {
        // GIVEN
        val now = Date()

        // WHEN
        Thread.sleep(1000L)
        rest.delete(url(199))

        // THEN
        val discount = dao.findById(199L).get()
        assertTrue(discount.isDeleted)
        assertTrue(discount.deleted!!.before(now))
    }

    private fun url(id: Long) = "http://localhost:$port/v1/discounts/$id"
}
