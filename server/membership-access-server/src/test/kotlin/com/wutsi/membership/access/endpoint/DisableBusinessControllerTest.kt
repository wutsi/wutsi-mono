package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.dao.AccountRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DisableBusinessController.sql"])
class DisableBusinessControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: AccountRepository

    @Test
    fun disable() {
        // WHEN
        rest.delete(url(100))

        // THEN
        val account = dao.findById(100).get()
        assertFalse(account.business)
        assertNotNull(account.businessId)
        assertNotNull(account.storeId)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/accounts/$id/business"
}
