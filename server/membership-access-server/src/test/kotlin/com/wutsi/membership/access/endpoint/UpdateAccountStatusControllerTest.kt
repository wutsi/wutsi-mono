package com.wutsi.membership.access.endpoint

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dao.AccountRepository
import com.wutsi.membership.access.dao.NameRepository
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateAccountStatusController.sql"])
class UpdateAccountStatusControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var nameDao: NameRepository

    @Test
    fun activate() {
        val request = UpdateAccountStatusRequest(
            status = AccountStatus.ACTIVE.name,
        )
        val response = rest.postForEntity(url(199), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(199).get()
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertNull(account.deactivated)
    }

    @Test
    fun suspend() {
        val request = UpdateAccountStatusRequest(
            status = AccountStatus.INACTIVE.name,
        )
        val response = rest.postForEntity(url(100), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(AccountStatus.INACTIVE, account.status)
        assertNotNull(account.deactivated)
        assertNull(account.name)

        val name = nameDao.findById(100)
        assertTrue(name.isEmpty)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/accounts/$id/status"
}
