package com.wutsi.blog.transaction.it

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.dto.CreateWalletCommand
import com.wutsi.blog.transaction.dto.CreateWalletResponse
import com.wutsi.blog.transaction.dto.WalletCreatedEventPayload
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/CreateWalletCommand.sql"])
class CreateWalletCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: WalletRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Test
    fun create() {
        // WHEN
        val command = CreateWalletCommand(
            userId = 10L,
            country = "CM",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/create", command, CreateWalletResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val wallet = dao.findById(result.body!!.walletId).get()
        assertEquals(command.userId, wallet.user.id)
        assertEquals("XAF", wallet.currency)

        val events = eventStore.events(
            streamId = StreamId.WALLET,
            entityId = wallet.id,
            userId = command.userId.toString(),
            type = EventType.WALLET_CREATED_EVENT,
        )
        assertTrue(events.isNotEmpty())
        val payload = events[0].payload as WalletCreatedEventPayload
        assertEquals(command.country, payload.country)

        Thread.sleep(15000)
        val user = userDao.findById(command.userId).get()
        assertEquals(wallet.id, user.walletId)
    }

    @Test
    fun countryNotSupportMonetization() {
        // WHEN
        val command = CreateWalletCommand(
            userId = 11L,
            country = "XX",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/create", command, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.COUNTRY_DONT_SUPPORT_WALLET, result.body!!.error.code)
    }

    @Test
    fun walletAlreadyCreated() {
        // WHEN
        val command = CreateWalletCommand(
            userId = 20L,
            country = "CM",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/create", command, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.WALLET_ALREADY_CREATED, result.body!!.error.code)
    }

    @Test
    fun userNotSupportMonetization() {
        // WHEN
        val command = CreateWalletCommand(
            userId = 30,
            country = "CM",
        )
        val result =
            rest.postForEntity("/v1/wallets/commands/create", command, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.USER_DONT_SUPPORT_WALLET, result.body!!.error.code)
    }
}
