package com.wutsi.blog.transaction.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.dto.SubmitCashoutResponse
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreateTransferResponse
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/SubmitCashoutCommand.sql"])
class SubmitCashoutCommandTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

    @Autowired
    private lateinit var walletDao: WalletRepository

    @MockBean
    private lateinit var flutterwave: FWGateway

    private var accessToken: String? = "session-ray"

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        accessToken?.let {
            request.headers.setBearerAuth(it)
        }
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        rest.restTemplate.interceptors = listOf(this)

        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @Test
    fun pending() {
        // Given
        accessToken = "ray-1"

        val response = CreateTransferResponse(
            transactionId = UUID.randomUUID().toString(),
            financialTransactionId = UUID.randomUUID().toString(),
            status = Status.PENDING,
        )
        doReturn(response).whenever(flutterwave).createTransfer(any())

        // WHEN
        val command = SubmitCashoutCommand(
            walletId = "1",
            amount = 500,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-cashout", command, SubmitCashoutResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CASHOUT, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNull(tx.user)
        assertEquals(command.walletId, tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals("ray.sponsible@gmail.com", tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals("Ray Sponsible", tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals("+237999999991", tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertEquals(response.transactionId, tx.gatewayTransactionId)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val wallet = walletDao.findById(command.walletId).get()
        assertEquals(400L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
    }

    @Test
    fun error() {
        // Given
        accessToken = "ray-2"

        val ex = PaymentException(
            error = Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).createTransfer(any())

        // WHEN
        val command = SubmitCashoutCommand(
            walletId = "2",
            amount = 500,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-cashout", command, SubmitCashoutResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ex.error.code.name, result.body!!.errorCode)
        assertEquals(ex.error.message, result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CASHOUT, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNull(tx.user?.id)
        assertEquals(command.walletId, tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals("login@gmail.com", tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals("Jane Doe", tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals("+237999999992", tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0L, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertEquals(ex.error.transactionId, tx.gatewayTransactionId)
        assertEquals(ex.error.code.name, tx.errorCode)
        assertEquals(ex.error.message, tx.errorMessage)
        assertEquals(ex.error.supplierErrorCode, tx.supplierErrorCode)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_FAILED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val wallet = walletDao.findById(command.walletId).get()
        assertEquals(900L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
    }

    @Test
    fun idempotency() {
        // GIVEN
        accessToken = "ray-4"

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = SubmitCashoutCommand(
            walletId = "4",
            amount = 500,
            currency = "XAF",
            idempotencyKey = "donation-400",
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-cashout", command, SubmitCashoutResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("400", result.body!!.transactionId)
        assertEquals(Status.PENDING.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = result.body!!.transactionId,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertFalse(events.isNotEmpty())

        val tx = dao.findById(result.body!!.transactionId).get()
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun notEnoughFunds() {
        // WHEN
        accessToken = "ray-1"

        val command = SubmitCashoutCommand(
            walletId = "1",
            amount = 1000000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-cashout", command, SubmitCashoutResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ErrorCode.NOT_ENOUGH_FUNDS.name, result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CASHOUT, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNull(tx.user?.id)
        assertEquals(command.walletId, tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals("ray.sponsible@gmail.com", tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals("Ray Sponsible", tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals("+237999999991", tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0L, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertNull(tx.gatewayTransactionId)
        assertEquals(ErrorCode.NOT_ENOUGH_FUNDS.name, tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_FAILED_EVENT,
        )
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun forbidden() {
        // Given
        accessToken = "ray-2"

        // WHEN
        val command = SubmitCashoutCommand(
            walletId = "1",
            amount = 1,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-cashout", command, SubmitCashoutResponse::class.java)

        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }
}
