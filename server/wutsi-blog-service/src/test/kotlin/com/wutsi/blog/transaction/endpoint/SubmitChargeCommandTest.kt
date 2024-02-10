package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.CouponRepository
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitChargeResponse
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dao.UserRepository
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
import org.junit.jupiter.api.Assertions.assertNotNull
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/SubmitChargeCommand.sql"])
class SubmitChargeCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var walletDao: WalletRepository

    @Autowired
    private lateinit var couponDao: CouponRepository

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
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val cmd = argumentCaptor<CreatePaymentRequest>()
        verify(flutterwave).createPayment(cmd.capture())
        assertEquals(command.amount.toDouble(), cmd.firstValue.amount.value)
        assertEquals(command.currency, cmd.firstValue.amount.currency)
        assertEquals(result.body!!.transactionId, cmd.firstValue.externalId)
        assertEquals("1", cmd.firstValue.walletId)
        assertEquals("product 1", cmd.firstValue.description)
        assertEquals("1", cmd.firstValue.payer.id)
        assertEquals(command.paymentNumber, cmd.firstValue.payer.phoneNumber)
        assertEquals(command.email, cmd.firstValue.payer.email)
        assertEquals("CM", cmd.firstValue.payer.country)
        assertEquals(command.paymentMethodOwner, cmd.firstValue.payer.fullName)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1, tx.user?.id)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(0L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
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
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.PAYPAL,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com",
            internationalCurrency = "EUR"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val cmd = argumentCaptor<CreatePaymentRequest>()
        verify(paypal).createPayment(cmd.capture())
        assertEquals(2.0, cmd.firstValue.amount.value)
        assertEquals(command.internationalCurrency, cmd.firstValue.amount.currency)
        assertEquals(result.body!!.transactionId, cmd.firstValue.externalId)
        assertEquals("1", cmd.firstValue.walletId)
        assertEquals("product 1", cmd.firstValue.description)
        assertEquals("1", cmd.firstValue.payer.id)
        assertEquals(command.paymentNumber, cmd.firstValue.payer.phoneNumber)
        assertEquals(command.email, cmd.firstValue.payer.email)
        assertEquals("CM", cmd.firstValue.payer.country)
        assertEquals(command.paymentMethodOwner, cmd.firstValue.payer.fullName)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.PAYPAL, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1, tx.user?.id)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(0L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
    }

    @Test
    fun free() {
        // GIVEN

        // WHEN
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 0,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.NONE,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "",
            email = "ray.sponsible0000@gmail.com"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.PENDING.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.NONE, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNotNull(tx.user)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
        assertEquals(command.amount, tx.amount)
        assertEquals(command.currency, tx.currency)
        assertEquals(command.email, tx.email)
        assertNull(tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals(command.paymentMethodOwner, tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.NONE, tx.paymentMethodType)
        assertEquals(command.paymentNumber, tx.paymentMethodNumber)
        assertEquals(0L, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(0L, tx.gatewayFees)
        assertEquals(command.amount, tx.amount)
        assertNotNull(tx.gatewayTransactionId)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertNull(tx.internationalAmount)
        assertNull(tx.internationalCurrency)
        assertNull(tx.exchangeRate)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val user = userDao.findByEmailIgnoreCase(command.email!!).get()
        assertNull(user.country)
        assertEquals(tx.user?.id, user.id)

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(0L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
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
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible1111@gmail.com"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ex.error.code.name, result.body!!.errorCode)
        assertEquals(ex.error.message, result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNotNull(tx.user)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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

        val user = userDao.findByEmailIgnoreCase(command.email!!).get()
        assertEquals("cm", user.country)
        assertEquals(tx.user?.id, user.id)
    }

    @Test
    fun idempotency() {
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = "charge-100",
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com"
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

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

    @Test
    fun coupon() {
        // GIVEN
        val response = CreatePaymentResponse(
            transactionId = UUID.randomUUID().toString(),
            financialTransactionId = UUID.randomUUID().toString(),
            status = Status.PENDING,
        )
        doReturn(response).whenever(flutterwave).createPayment(any())

        // WHEN
        val command = SubmitChargeCommand(
            productId = 1L,
            userId = 1L,
            amount = 600,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible@gmail.com",
            discountType = DiscountType.COUPON,
            couponId = 1L
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.status.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        val cmd = argumentCaptor<CreatePaymentRequest>()
        verify(flutterwave).createPayment(cmd.capture())
        assertEquals(command.amount.toDouble(), cmd.firstValue.amount.value)
        assertEquals(command.currency, cmd.firstValue.amount.currency)
        assertEquals(result.body!!.transactionId, cmd.firstValue.externalId)
        assertEquals("1", cmd.firstValue.walletId)
        assertEquals("product 1", cmd.firstValue.description)
        assertEquals("1", cmd.firstValue.payer.id)
        assertEquals(command.paymentNumber, cmd.firstValue.payer.phoneNumber)
        assertEquals(command.email, cmd.firstValue.payer.email)
        assertEquals("CM", cmd.firstValue.payer.country)
        assertEquals(command.paymentMethodOwner, cmd.firstValue.payer.fullName)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.PENDING, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertEquals(1, tx.user?.id)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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
        assertEquals(command.discountType, tx.discountType)
        assertEquals(command.couponId, tx.coupon?.id)
        assertNull(tx.internationalAmount)
        assertNull(tx.internationalCurrency)
        assertNull(tx.exchangeRate)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val coupon = couponDao.findById(command.couponId).get()
        assertEquals(tx.id, coupon.transaction?.id)

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(0L, wallet.balance)
        assertNull(wallet.lastCashoutDateTime)
        assertNull(wallet.nextCashoutDate)
    }

    @Test
    fun `used coupon`() {
        // WHEN
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible1111@gmail.com",
            discountType = DiscountType.COUPON,
            couponId = 200L,
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(com.wutsi.blog.error.ErrorCode.COUPON_ALREADY_USED, result.body!!.errorCode)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNotNull(tx.user)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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
        assertEquals(com.wutsi.blog.error.ErrorCode.COUPON_ALREADY_USED, tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertEquals(command.discountType, tx.discountType)
        assertEquals(command.couponId, tx.coupon?.id)
        assertNull(tx.internationalAmount)
        assertNull(tx.internationalCurrency)
        assertNull(tx.exchangeRate)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_FAILED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        val coupon = couponDao.findById(command.couponId!!).get()
        assertEquals("200", coupon.transaction?.id)
    }

    @Test
    fun `expired coupon`() {
        // WHEN
        val command = SubmitChargeCommand(
            productId = 1L,
            amount = 1000,
            currency = "XAF",
            idempotencyKey = UUID.randomUUID().toString(),
            paymentMethodType = PaymentMethodType.MOBILE_MONEY,
            paymentMethodOwner = "Ray Sponsible",
            paymentNumber = "+237971111111",
            email = "ray.sponsible1111@gmail.com",
            discountType = DiscountType.COUPON,
            couponId = 201L,
        )
        val result =
            rest.postForEntity("/v1/transactions/commands/submit-charge", command, SubmitChargeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(com.wutsi.blog.error.ErrorCode.COUPON_EXPIRED, result.body!!.errorCode)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(TransactionType.CHARGE, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(command.idempotencyKey, tx.idempotencyKey)
        assertNotNull(tx.user)
        assertEquals("100", tx.store?.id)
        assertEquals("1", tx.wallet.id)
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
        assertEquals(com.wutsi.blog.error.ErrorCode.COUPON_EXPIRED, tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertEquals(command.discountType, tx.discountType)
        assertEquals(command.couponId, tx.coupon?.id)
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
}
