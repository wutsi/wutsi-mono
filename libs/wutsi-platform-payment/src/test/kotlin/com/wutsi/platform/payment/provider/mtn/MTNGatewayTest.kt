package com.wutsi.platform.payment.provider.mtn

import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.mtn.product.Collection
import com.wutsi.platform.payment.provider.mtn.product.Disbursement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.Ignore
import kotlin.test.assertFalse
import kotlin.test.assertNull

/**
 * Test are failing in the CI/CD - SSL errors :-(
 */
internal class MTNGatewayTest {
    private val gateway: Gateway = createGateway()

    @BeforeEach
    fun setUp() {
        Thread.sleep(5000) // Sleep to prevent 429 error
    }

    @Test
    fun type() {
        assertEquals(GatewayType.MTN, gateway.getType())
    }

    @Test
    @Ignore
    fun `create payment`() {
        val request = createCreatePaymentRequest(Fixtures.NUMBER_SUCCESS)
        val response = gateway.createPayment(request)

        assertNotNull(response.transactionId)
        assertEquals(Status.SUCCESSFUL, response.status)
        assertNotNull(response.financialTransactionId)
    }

    @Test
    @Ignore
    fun `create payment - failure`() {
        val request = createCreatePaymentRequest(Fixtures.NUMBER_FAILED)
        val ex = assertThrows<PaymentException> {
            gateway.createPayment(request)
        }

        assertFalse(ex.error.transactionId.isNullOrBlank())
        assertEquals(ErrorCode.INTERNAL_PROCESSING_ERROR, ex.error.code)
        assertEquals("INTERNAL_PROCESSING_ERROR", ex.error.supplierErrorCode)
    }

    @Test
    @Ignore
    fun `create payment - timeout`() {
        val request = createCreatePaymentRequest(Fixtures.NUMBER_TIMEOUT)
        val ex = assertThrows<PaymentException> {
            gateway.createPayment(request)
        }
        val ex2 = assertThrows<PaymentException> {
            gateway.getPayment(ex.error.transactionId)
        }

        assertEquals(ex.error.transactionId, ex2.error.transactionId)
        assertEquals(ErrorCode.EXPIRED, ex.error.code)
        assertEquals("EXPIRED", ex.error.supplierErrorCode)
    }

    @Test
    @Ignore
    fun `get payment information of PENDING transaction`() {
        val request = createCreatePaymentRequest(Fixtures.NUMBER_PENDING)
        val resp = gateway.createPayment(request)
        val response = gateway.getPayment(resp.transactionId)

        assertEquals(Status.PENDING, response.status)
        assertEquals(request.amount.value, response.amount.value)
        assertEquals("EUR", response.amount.currency)
        assertEquals(request.payerMessage, response.payerMessage)
        assertEquals(request.description, response.description)
        assertEquals(request.externalId, response.externalId)
        assertNull(response.financialTransactionId)
    }

    @Test
    @Ignore
    fun `get payment information of SUCESSFULL transaction`() {
        val request = createCreatePaymentRequest(Fixtures.NUMBER_SUCCESS)
        val resp = gateway.createPayment(request)
        val response = gateway.getPayment(resp.transactionId)

        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(request.amount.value, response.amount.value)
        assertEquals("EUR", response.amount.currency)
        assertEquals(request.payerMessage, response.payerMessage)
        assertEquals(request.description, response.description)
        assertEquals(request.externalId, response.externalId)
        assertNotNull(response.financialTransactionId)
    }

    @Test
    @Ignore
    fun `create transfer`() {
        val request = createCreateTransferRequest(Fixtures.NUMBER_SUCCESS)
        val response = gateway.createTransfer(request)

        assertNotNull(response.transactionId)
        assertNotNull(response.financialTransactionId)
        assertEquals(Status.SUCCESSFUL, response.status)
    }

    @Test
    @Ignore
    fun `create transfer - failure`() {
        val request = createCreateTransferRequest(Fixtures.NUMBER_FAILED)
        val ex = assertThrows<PaymentException> {
            gateway.createTransfer(request)
        }

        assertFalse(ex.error.transactionId.isNullOrBlank())
        assertEquals(ErrorCode.INTERNAL_PROCESSING_ERROR, ex.error.code)
        assertEquals("INTERNAL_PROCESSING_ERROR", ex.error.supplierErrorCode)
    }

    @Test
    @Ignore
    fun `get transfer information - PENDING`() {
        val request = createCreateTransferRequest(Fixtures.NUMBER_PENDING)
        val resp = gateway.createTransfer(request)
        val response = gateway.getTransfer(resp.transactionId)

        assertEquals(Status.PENDING, response.status)
        assertEquals(request.amount.value, response.amount.value)
        assertEquals("EUR", response.amount.currency)
        assertEquals(request.payerMessage, response.payerMessage)
        assertEquals(request.description, response.description)
        assertEquals(request.externalId, response.externalId)
        assertNull(response.financialTransactionId)
    }

    @Test
    @Ignore
    fun `get transfer information - FAILURE`() {
        val request = createCreateTransferRequest(Fixtures.NUMBER_TIMEOUT)
        val ex = assertThrows<PaymentException> {
            gateway.createTransfer(request)
        }

        assertEquals(ErrorCode.EXPIRED, ex.error.code)
        assertNotNull(ex.error.transactionId)
        assertEquals("EXPIRED", ex.error.supplierErrorCode)
    }

    private fun createGateway() =
        MTNGateway(
            collection = Collection(
                config = Fixtures.createCollectionConfig(),
                http = Fixtures.createHttp(),
            ),
            disbursement = Disbursement(
                config = Fixtures.createDisbursementConfig(),
                http = Fixtures.createHttp(),
            ),
        )

    private fun createCreatePaymentRequest(phoneNumber: String) = CreatePaymentRequest(
        payer = Party(
            fullName = "Ray Sponsible",
            phoneNumber = phoneNumber,
        ),
        amount = Money(
            value = 100.0,
            currency = "EUR",
        ),
        payerMessage = "Hello wold",
        externalId = "1111",
        description = "Sample product",
    )

    private fun createCreateTransferRequest(phoneNumber: String) = CreateTransferRequest(
        payee = Party(
            fullName = "Ray Sponsible",
            phoneNumber = phoneNumber,
        ),
        amount = Money(
            value = 100.0,
            currency = "XAF",
        ),
        payerMessage = "Hello wold",
        externalId = "1111",
        description = "Sample product",
    )
}
