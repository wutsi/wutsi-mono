package com.wutsi.blog.transaction.it

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.platform.core.error.ErrorResponse
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
@Sql(value = ["/db/clean.sql", "/db/transaction/GetWalletQuery.sql"])
class GetWalletQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        // WHEN
        val result = rest.getForEntity("/v1/wallets/10", GetWalletResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val wallet = result.body!!.wallet
        assertEquals(10, wallet.userId)
        assertEquals("XAF", wallet.currency)
        assertEquals("CM", wallet.country)
        assertEquals(10000, wallet.balance)
    }

    @Test
    fun notFound() {
        // WHEN
        val result = rest.getForEntity("/v1/wallets/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.WALLET_NOT_FOUND, result.body!!.error.code)
    }
}
