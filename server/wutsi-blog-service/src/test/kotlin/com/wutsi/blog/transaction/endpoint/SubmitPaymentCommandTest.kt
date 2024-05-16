package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitPaymentCommand
import com.wutsi.blog.transaction.dto.SubmitPaymentResponse
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreatePaymentResponse
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import com.wutsi.platform.payment.provider.paypal.Paypal
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/SubmitPaymentCommand.sql"])
class SubmitPaymentCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

    @MockBean
    private lateinit var flutterwave: Flutterwave

    @MockBean
    private lateinit var paypal: Paypal

    @BeforeEach
    fun setUp() {
        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
        doReturn(GatewayType.PAYPAL).whenever(paypal).getType()
    }

    @Test
    fun pending() {
        // GIVEN
        val response = CreatePaymentResponse(
            transactionId = UUID.randomUUID().toString(),
            financialTransactionId = UUID.randomUUID().toString(),
            status = Status.PENDING,
        )
        doReturn(response).whenever(flutterwave).createPayment(any())

        // WHEN
        val command = SubmitPaymentCommand(
            adsId = "100",
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com",
            userId = 1L,
            channel = "xxx"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-payment", command, SubmitPaymentResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val cmd = argumentCaptor<CreatePaymentRequest>()
        verify(flutterwave).createPayment(cmd.capture())
        assertEquals(command.amount.toDouble(), cmd.firstValue.amount.value)
        assertEquals(command.currency, cmd.firstValue.amount.currency)
        assertEquals(result.body!!.transactionId, cmd.firstValue.externalId)
        assertNull(cmd.firstValue.walletId)
        assertEquals("Ads 1", cmd.firstValue.description)
        assertEquals("1", cmd.firstValue.payer.id)
        assertEquals(command.paymentNumber, cmd.firstValue.payer.phoneNumber)
        assertEquals(command.email, cmd.firstValue.payer.email)
        assertEquals("CM", cmd.firstValue.payer.country)
        assertEquals(command.paymentMethodOwner, cmd.firstValue.payer.fullName)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1, tx.user?.id)
        assertNull(tx.store)
        assertNull(tx.wallet)
        assertEquals(command.adsId, tx.ads?.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals("ray.sponsible@gmail.com", tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertEquals(response.transactionId, tx.gatewayTransactionId)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertNull(tx.discountType)
        assertNull(tx.coupon)
        assertNull(tx.internationalAmount)
        assertNull(tx.internationalCurrency)
        assertNull(tx.exchangeRate)
        assertEquals(command.channel, tx.channel)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun paypal() {
        // GIVEN
        val response = CreatePaymentResponse(
            transactionId = UUID.randomUUID().toString(),
            financialTransactionId = UUID.randomUUID().toString(),
            status = Status.PENDING,
        )
        doReturn(response).whenever(paypal).createPayment(any())

        // WHEN
        val command = SubmitPaymentCommand(
            adsId = "100",
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.PAYPAL,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com",
            internationalCurrency = "EUR",
            userId = 1L,
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-payment", command, SubmitPaymentResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val cmd = argumentCaptor<CreatePaymentRequest>()
        verify(paypal).createPayment(cmd.capture())
        assertEquals(2.0, cmd.firstValue.amount.value)
        assertEquals(command.internationalCurrency, cmd.firstValue.amount.currency)
        assertEquals(result.body!!.transactionId, cmd.firstValue.externalId)
        assertNull(cmd.firstValue.walletId)
        assertEquals("Ads 1", cmd.firstValue.description)
        assertEquals("1", cmd.firstValue.payer.id)
        assertEquals(command.paymentNumber, cmd.firstValue.payer.phoneNumber)
        assertEquals(command.email, cmd.firstValue.payer.email)
        assertEquals("CM", cmd.firstValue.payer.country)
        assertEquals(command.paymentMethodOwner, cmd.firstValue.payer.fullName)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(GatewayType.PAYPAL, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1, tx.user?.id)
        assertEquals(command.adsId, tx.ads?.id)
        assertNull(tx.store)
        assertNull(tx.wallet)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals("ray.sponsible@gmail.com", tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.PAYPAL, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertEquals(response.transactionId, tx.gatewayTransactionId)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertNull(tx.discountType)
        assertNull(tx.coupon)
        assertEquals(2L, tx.internationalAmount)
        assertEquals(command.internationalCurrency, tx.internationalCurrency)
        assertEquals(1.0 / 656.0, tx.exchangeRate)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun error() {
        // GIVEN
        val ex = PaymentException(
            error = Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).createPayment(any())

        // WHEN
        val command = SubmitPaymentCommand(
            adsId = "100",
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible1111@gmail.com",
            userId = 1L,
        )
        val result =
            rest.postForEntity(
                "/v1/transactions/commands/submit-payment",
                command,
                SubmitPaymentResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ex.error.code.name, result.body!!.errorCode)
        assertEquals(ex.error.message, result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1L, tx.user?.id)
        assertNull(tx.store)
        assertNull(tx.wallet)
        assertEquals(command.adsId, tx.ads?.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals(command.email, tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertEquals(ex.error.transactionId, tx.gatewayTransactionId)
        assertEquals(ex.error.code.name, tx.errorCode)
        assertEquals(ex.error.message, tx.errorMessage)
        assertEquals(ex.error.supplierErrorCode, tx.supplierErrorCode)
        assertNull(tx.internationalAmount)
        assertNull(tx.internationalCurrency)
        assertNull(tx.exchangeRate)

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
        val command = SubmitPaymentCommand(
            adsId = "100",
            amount = 1000,
            currency = "XAF",
            idempotencyKey = "payment-100",
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-payment", command, SubmitPaymentResponse::class.java)

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
