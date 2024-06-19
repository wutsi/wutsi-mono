package com.wutsi.blog.transaction.endpoint

import com.wutsi.blog.transaction.dto.SearchSuperFanRequest
import com.wutsi.blog.transaction.dto.SearchSuperFanResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/SearchSuperFanQuery.sql"])
class SearchSuperFanQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun searchByWallet() {
        // WHEN
        val request = SearchSuperFanRequest(
            walletId = "1",
        )
        val result =
            rest.postForEntity("/v1/super-fans/queries/search", request, SearchSuperFanResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val fans = result.body!!.superFans.sortedBy { it.userId }
        assertEquals(2, fans.size)

        assertEquals(2L, fans[0].userId)
        assertEquals("1", fans[0].walletId)
        assertEquals(1L, fans[0].transactionCount)
        assertEquals(5000L, fans[0].value)

        assertEquals(3L, fans[1].userId)
        assertEquals("1", fans[1].walletId)
        assertEquals(2L, fans[1].transactionCount)
        assertEquals(51000L, fans[1].value)
    }

    @Test
    fun searchByUser() {
        // WHEN
        val request = SearchSuperFanRequest(
            userId = 2L
        )
        val result =
            rest.postForEntity("/v1/super-fans/queries/search", request, SearchSuperFanResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val fans = result.body!!.superFans.sortedBy { it.userId }
        assertEquals(1, fans.size)

        assertEquals(2L, fans[0].userId)
        assertEquals("1", fans[0].walletId)
        assertEquals(1L, fans[0].transactionCount)
        assertEquals(5000L, fans[0].value)
    }
}
