package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.Fixtures.createFWWebhookRequest
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
@Sql(value = ["/db/clean.sql", "/db/transaction/FlutterwaveWebhookDonation.sql"])
class FlutterwaveWebhookDonationTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

    @Autowired
    private lateinit var walletDao: WalletRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var adsDao: AdsRepository

    @MockBean
    private lateinit var flutterwave: Flutterwave

    @Value("\${wutsi.platform.payment.flutterwave.secret-hash}")
    private lateinit var secretHash: String

    @Value("\${wutsi.application.transaction.donation.fees-percentage}")
    private lateinit var donationFeesPercent: java.lang.Double

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers.add("verif-hash", secretHash)
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        rest.restTemplate.interceptors = listOf(this)

        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @Test
    fun pendingToSuccess() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "100"

        val response = GetPaymentResponse(
            fees = Money(100.0, "XAF"),
            status = Status.SUCCESSFUL,
        )
        doReturn(response).whenever(flutterwave).getPayment(any())

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val amount = 10000L
        val fees = (amount * donationFeesPercent.toDouble()).toLong()
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals(response.fees.value.toLong(), tx.gatewayFees)
        assertEquals(fees, tx.fees)
        assertEquals(amount - fees, tx.net)
        assertEquals(amount, tx.amount)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(10500, wallet.balance)
        assertEquals(2, wallet.donationCount)
        assertTrue(wallet.lastModificationDateTime.after(now))

        val user = userDao.findById(wallet.user.id).get()
        assertEquals(wallet.donationCount, user.donationCount)
        assertEquals(2, user.superFanCount)

        val ads = adsDao.findById("ads-100").get()
        assertEquals(2, ads.orderCount)
        assertEquals(20000, ads.totalSales)
    }

    @Test
    fun pendingToFailed() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "200"

        val ex = PaymentException(
            error = Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).getPayment(any())

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.FAILED, tx.status)
        assertEquals(0, tx.gatewayFees)
        assertEquals(0, tx.fees)
        assertEquals(0, tx.net)
        assertEquals(10000, tx.amount)
        assertEquals(ex.error.code.name, tx.errorCode)
        assertEquals(ex.error.message, tx.errorMessage)
        assertEquals(ex.error.supplierErrorCode, tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))

        Thread.sleep(15000)
        val wallet = walletDao.findById("2").get()
        assertEquals(450, wallet.balance)
        assertEquals(1, wallet.donationCount)
        assertFalse(wallet.lastModificationDateTime.after(now))
    }

    @Test
    fun successToFailed() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "300"

        val ex = PaymentException(
            error = Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).getPayment(any())

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun successToSuccess() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "300"

        val response = GetPaymentResponse(
            fees = Money(100.0, "XAF"),
            status = Status.SUCCESSFUL,
        )
        doReturn(response).whenever(flutterwave).getPayment(transactionId)

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun failedToFailed() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "310"

        val ex = PaymentException(
            error = Error(
                code = ErrorCode.DECLINED,
                transactionId = UUID.randomUUID().toString(),
                supplierErrorCode = "1111",
                message = "This is an error",
            ),
        )
        doThrow(ex).whenever(flutterwave).getPayment(any())

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.FAILED, tx.status)
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun failedToSuccess() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)
        val transactionId = "310"

        val response = GetPaymentResponse(
            fees = Money(100.0, "XAF"),
            status = Status.SUCCESSFUL,
        )
        doReturn(response).whenever(flutterwave).getPayment(transactionId)

        // WHEN
        val request = createFWWebhookRequest(transactionId)
        val result = rest.postForEntity("/webhooks/flutterwave", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.FAILED, tx.status)
        assertFalse(tx.lastModificationDateTime.after(now))
    }
}
