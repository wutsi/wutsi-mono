package com.wutsi.blog.transaction.it

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.UpdateWalletAccountCommand
import com.wutsi.blog.transaction.dto.WalletAccountUpdatedEventPayload
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/UpdateWalletAccountCommand.sql"])
class UpdateWalletAccountCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: WalletRepository

    @Test
    fun create() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = UpdateWalletAccountCommand(
            walletId = "10",
            type = PaymentMethodType.MOBILE_MONEY,
            owner = "Ray Sponsible",
            number = "+237655000000",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/update-account", command, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val wallet = dao.findById("10").get()
        assertEquals(command.type, wallet.accountType)
        assertEquals(command.owner, wallet.accountOwner)
        assertEquals(command.number, wallet.accountNumber)
        assertTrue(wallet.lastModificationDateTime.after(now))

        val events = eventStore.events(
            streamId = StreamId.WALLET,
            entityId = wallet.id,
            type = EventType.WALLET_ACCOUNT_UPDATED_EVENT,
        )
        assertTrue(events.isNotEmpty())
        val payload = events[0].payload as WalletAccountUpdatedEventPayload
        assertEquals(command.type, payload.type)
        assertEquals(command.owner, payload.owner)
        assertEquals(command.number, payload.number)
    }

    @Test
    fun invalidPhoneNumber() {
        // WHEN
        val command = UpdateWalletAccountCommand(
            walletId = "20",
            type = PaymentMethodType.MOBILE_MONEY,
            owner = "Ray Sponsible",
            number = "+237111000000",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/update-account", command, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)

        assertEquals(ErrorCode.WALLET_ACCOUNT_NUMNER_INVALID, result.body!!.error.code)
    }

    @Test
    fun countryNotSupportMonetization() {
        // WHEN
        val command = UpdateWalletAccountCommand(
            walletId = "30",
            type = PaymentMethodType.MOBILE_MONEY,
            owner = "Ray Sponsible",
            number = "+237111000000",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/update-account", command, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.COUNTRY_DONT_SUPPORT_WALLET, result.body!!.error.code)
    }
}
