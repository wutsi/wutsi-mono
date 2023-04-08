package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.dao.FundraisingRepository
import com.wutsi.marketplace.access.dto.CreateFundraisingRequest
import com.wutsi.marketplace.access.dto.CreateFundraisingResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateFundraisingController.sql"])
public class CreateFundraisingControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: FundraisingRepository

    private val rest = RestTemplate()

    @Test
    fun create() {
        val request = CreateFundraisingRequest(
            accountId = 555,
            businessId = 333,
            currency = "USD",
            amount = 1000,
        )
        val response = rest.postForEntity(url(), request, CreateFundraisingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = dao.findById(response.body!!.fundraisingId)
        assertTrue(fundraising.isPresent)
        assertEquals(request.accountId, fundraising.get().accountId)
        assertEquals(request.businessId, fundraising.get().businessId)
        assertEquals(request.currency, fundraising.get().currency)
        assertEquals(FundraisingStatus.ACTIVE, fundraising.get().status)
        assertNotNull(fundraising.get().created)
        assertEquals(request.amount, fundraising.get().amount)
        assertNull(fundraising.get().description)
        assertNull(fundraising.get().videoUrl)
    }

    @Test
    fun duplicate() {
        val request = CreateFundraisingRequest(
            accountId = 1,
            currency = "USD",
        )
        val response = rest.postForEntity(url(), request, CreateFundraisingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertEquals(100L, response.body!!.fundraisingId)
    }

    private fun url() = "http://localhost:$port/v1/fundraisings"
}
