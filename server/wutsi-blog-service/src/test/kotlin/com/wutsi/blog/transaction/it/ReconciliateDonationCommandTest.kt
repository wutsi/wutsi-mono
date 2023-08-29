package com.wutsi.blog.transaction.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitDonationResponse
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentResponse
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
@Sql(value = ["/db/clean.sql", "/db/transaction/SubmitDonationCommand.sql"])
class SubmitDonationCommandTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

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
        val response = CreatePaymentResponse(
            transactionId = UUID.randomUUID().toString(),
            financialTransactionId = UUID.randomUUID().toString(),
            status = Status.PENDING,
        )
        doReturn(response).whenever(flutterwave).createPayment(any())

        // WHEN
        val command = SubmitDonationCommand(
            userId = 1L,
            walletId = "2",
            amount = 10000,
            currency = "XAF",
            email = "ray.sponsible@gmail.com",
            description = "Test donation",
            anonymous = true,
            paymentNumber = "+237971111111",
            paymentMethodOwner = "Ray Sponsible",
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-donation", command, SubmitDonationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(command.userId, tx.user?.id)
        assertEquals(command.walletId, tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals(command.email, tx.email)
        assertEquals(command.description, tx.description)
        assertEquals(command.anonymous, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(command.paymentMethodType, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
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
    }

    @Test
    fun error() {
        val ex = PaymentException(
            error = com.wutsi.platform.payment.core.Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).createPayment(any())

        // WHEN
        val command = SubmitDonationCommand(
            userId = 1L,
            walletId = "2",
            amount = 10000,
            currency = "XAF",
            email = "ray.sponsible@gmail.com",
            description = "Test donation",
            anonymous = true,
            paymentNumber = "+237971111111",
            paymentMethodOwner = "Ray Sponsible",
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            idempotencyKey = UUID.randomUUID().toString(),
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-donation", command, SubmitDonationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ex.error.code.name, result.body!!.errorCode)
        assertEquals(ex.error.message, result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(command.userId, tx.user?.id)
        assertEquals(command.walletId, tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals(command.email, tx.email)
        assertEquals(command.description, tx.description)
        assertEquals(command.anonymous, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(command.paymentMethodType, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
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
    }

    @Test
    fun idempotency() {
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = SubmitDonationCommand(
            userId = 1L,
            walletId = "2",
            amount = 10000,
            currency = "XAF",
            email = "ray.sponsible@gmail.com",
            description = "Test donation",
            anonymous = true,
            paymentNumber = "+237971111111",
            paymentMethodOwner = "Ray Sponsible",
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            idempotencyKey = "donation-100",
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-donation", command, SubmitDonationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("100", result.body!!.transactionId)
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
}
