package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
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
@Sql(value = ["/db/clean.sql", "/db/transaction/GetTransactionQuery.sql"])
class GetTransactionQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var flutterwave: Flutterwave

    @BeforeEach
    fun setUp() {
        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @Value("\${wutsi.application.transaction.donation.fees-percentage}")
    private lateinit var donationFeesPercent: java.lang.Double

    @Test
    fun get() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val result = rest.getForEntity("/v1/transactions/100", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = result.body!!.transaction
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(3L, tx.userId)
        assertEquals("XAF", tx.currency)
        assertEquals("1", tx.walletId)
        assertEquals(10000, tx.amount)
        assertEquals(0, tx.net)
        assertEquals(0, tx.fees)
        assertEquals(0, tx.gatewayFees)
        assertEquals("Roger Milla", tx.paymentMethodOwner)
        assertEquals("+237911111111", tx.paymentMethodNumber)
        assertEquals("roger.milla@gmail.com", tx.email)
        assertEquals(Status.PENDING, tx.status)
        assertEquals("100-100", tx.gatewayTransactionId)
        assertEquals("pending-100", tx.idempotencyKey)
        assertEquals("EMAIL", tx.channel)
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun syncSuccess() {
        // GIVEN
        val response = GetPaymentResponse(
            status = Status.SUCCESSFUL,
            fees = Money(100.0, "XAF"),
            amount = Money(10000.0, "XAF"),
            externalId = UUID.randomUUID().toString(),
        )
        doReturn(response).whenever(flutterwave).getPayment(any())

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val result = rest.getForEntity("/v1/transactions/200?sync=true", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val fees = (response.amount.value * donationFeesPercent.toDouble()).toLong()
        val tx = result.body!!.transaction
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(3L, tx.userId)
        assertEquals("XAF", tx.currency)
        assertEquals("2", tx.walletId)
        assertEquals(10000, tx.amount)
        assertEquals(10000 - fees, tx.net)
        assertEquals(fees, tx.fees)
        assertEquals(response.fees.value.toLong(), tx.gatewayFees)
        assertEquals("Roger Milla", tx.paymentMethodOwner)
        assertEquals("+237911111111", tx.paymentMethodNumber)
        assertEquals("roger.milla@gmail.com", tx.email)
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals("200", tx.gatewayTransactionId)
        assertEquals("pending-200", tx.idempotencyKey)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun syncPendingFree() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        val result = rest.getForEntity("/v1/transactions/110?sync=true", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val fees = 0L
        val tx = result.body!!.transaction
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(3L, tx.userId)
        assertEquals("XAF", tx.currency)
        assertEquals("1", tx.walletId)
        assertEquals(0L, tx.amount)
        assertEquals(0L, tx.net)
        assertEquals(fees, tx.fees)
        assertEquals(0L, tx.gatewayFees)
        assertEquals("Roger Milla", tx.paymentMethodOwner)
        assertEquals("+237911111111", tx.paymentMethodNumber)
        assertEquals("roger.milla@gmail.com", tx.email)
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals("110-100", tx.gatewayTransactionId)
        assertEquals("pending-free-110", tx.idempotencyKey)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun syncSuccessfulFree() {
        // WHEN
        val result = rest.getForEntity("/v1/transactions/111?sync=true", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val fees = 0L
        val tx = result.body!!.transaction
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(3L, tx.userId)
        assertEquals("XAF", tx.currency)
        assertEquals("1", tx.walletId)
        assertEquals(0L, tx.amount)
        assertEquals(0L, tx.net)
        assertEquals(fees, tx.fees)
        assertEquals(0L, tx.gatewayFees)
        assertEquals("Roger Milla", tx.paymentMethodOwner)
        assertEquals("+237911111111", tx.paymentMethodNumber)
        assertEquals("roger.milla@gmail.com", tx.email)
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals("111-100", tx.gatewayTransactionId)
        assertEquals("success-free-111", tx.idempotencyKey)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
    }

    @Test
    fun syncFailure() {
        // GIVEN
        val exception = PaymentException(
            error = Error(
                code = com.wutsi.platform.payment.core.ErrorCode.DECLINED,
                supplierErrorCode = "11111",
                errorId = "22222",
                message = "Transaction declined",
                transactionId = UUID.randomUUID().toString(),
            ),
        )
        doThrow(exception).whenever(flutterwave).getPayment(any())

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val result = rest.getForEntity("/v1/transactions/201?sync=true", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = result.body!!.transaction
        assertEquals(3L, tx.userId)
        assertEquals("XAF", tx.currency)
        assertEquals("2", tx.walletId)
        assertEquals(10000, tx.amount)
        assertEquals(0, tx.net)
        assertEquals(0, tx.fees)
        assertEquals(0, tx.gatewayFees)
        assertEquals("Roger Milla", tx.paymentMethodOwner)
        assertEquals("+237911111111", tx.paymentMethodNumber)
        assertEquals("roger.milla@gmail.com", tx.email)
        assertEquals(Status.FAILED, tx.status)
        assertEquals(exception.error.transactionId, tx.gatewayTransactionId)
        assertEquals("pending-201", tx.idempotencyKey)
        assertEquals(exception.error.code.name, tx.errorCode)
        assertEquals(exception.error.message, tx.errorMessage)
        assertEquals(exception.error.supplierErrorCode, tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun neverSyncSuccessfulTransaction() {
        val response = GetPaymentResponse(
            status = Status.SUCCESSFUL,
            fees = Money(100.0, "XAF"),
            amount = Money(10000.0, "XAF"),
            externalId = UUID.randomUUID().toString(),
        )
        doReturn(response).whenever(flutterwave).getPayment(any())

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val result = rest.getForEntity("/v1/transactions/300?sync=true", GetTransactionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = result.body!!.transaction
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals(50000, tx.amount)
        assertEquals(48000, tx.net)
        assertEquals(5000, tx.fees)
        assertEquals(0, tx.gatewayFees)
        assertFalse(tx.lastModificationDateTime.after(now))
    }

    @Test
    fun notFound() {
        // WHEN
        val result = rest.getForEntity("/v1/transactions/xxxxxx", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, result.body!!.error.code)
    }
}
