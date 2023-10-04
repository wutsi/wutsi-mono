package com.wutsi.platform.payment.provider.mtn

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.payment.Gateway
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.mtn.model.MTNParty
import com.wutsi.platform.payment.provider.mtn.model.MTNRequestToPayRequest
import com.wutsi.platform.payment.provider.mtn.model.MTNRequestToPayResponse
import com.wutsi.platform.payment.provider.mtn.model.MTNTokenResponse
import com.wutsi.platform.payment.provider.mtn.model.MTNTransferRequest
import com.wutsi.platform.payment.provider.mtn.model.MTNTransferResponse
import com.wutsi.platform.payment.provider.mtn.product.Collection
import com.wutsi.platform.payment.provider.mtn.product.Disbursement
import com.wutsi.platform.payment.provider.mtn.product.ProductConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.util.UUID

/**
 * Test are failing in the CI/CD - SSL errors :-(
 */
internal class MTNGatewayTest {
    private lateinit var http: Http
    private lateinit var gateway: Gateway
    private lateinit var userProvider: UserProvider
    private lateinit var config: ProductConfig

    private val user = User(
        id = "user-id",
        apiKey = "api-key",
    )
    private val payer = Party(
        fullName = "Ray Sponsible",
        phoneNumber = "+237111111111",
    )

    private val accessToken = UUID.randomUUID().toString()

    @BeforeEach
    fun setUp() {
        userProvider = mock()
        doReturn(user).whenever(userProvider).get()

        http = mock()

        config = ProductConfig(
            environment = Environment.SANDBOX,
            subscriptionKey = "subscription-key",
            callbackUrl = "https://callback.com/foo",
            userProvider = userProvider,
        )

        gateway = MTNGateway(
            collection = Collection(
                config = config,
                http = http,
            ),
            disbursement = Disbursement(
                config = config,
                http = http,
            ),
        )

        val tokenResponse = MTNTokenResponse(
            access_token = accessToken,
        )
        doReturn(tokenResponse).whenever(http).post(
            anyOrNull(),
            anyOrNull(),
            anyOrNull<Class<*>>(),
            anyOrNull<Class<MTNTokenResponse>>(),
            anyOrNull(),
        )
    }

    @Test
    fun `payment - SUCCESS`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "SUCCESSFUL",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreatePaymentRequest(Fixtures.NUMBER_SUCCESS)
        val response = gateway.createPayment(request)

        // THEN

        // Submit
        val referenceId = argumentCaptor<String>()
        val payload = argumentCaptor<MTNRequestToPayRequest>()
        val postHeaders = argumentCaptor<Map<String, String?>>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/collection/v1_0/requesttopay"),
            payload.capture(),
            eq(Any::class.java),
            postHeaders.capture(),
        )

        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(referenceId.firstValue, response.transactionId)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)

        assertEquals(request.amount.value.toString(), payload.firstValue.amount)
        assertEquals("EUR", payload.firstValue.currency)
        assertEquals(Fixtures.NUMBER_SUCCESS.substring(1), payload.firstValue.payer.partyId)
        assertEquals("MSISDN", payload.firstValue.payer.partyIdType)
        assertEquals(request.externalId, payload.firstValue.externalId)
        assertEquals(request.description, payload.firstValue.payeeNote)
        assertEquals(request.payerMessage, payload.firstValue.payerMessage)

        assertEquals("application/json", postHeaders.firstValue["Content-Type"])
        assertEquals("Bearer $accessToken", postHeaders.firstValue["Authorization"])
        assertEquals(config.callbackUrl, postHeaders.firstValue["X-Callback-Url"])
        assertEquals(config.environment.name.lowercase(), postHeaders.firstValue["X-Target-Environment"])
        assertEquals(config.subscriptionKey, postHeaders.firstValue["Ocp-Apim-Subscription-Key"])
        assertEquals(referenceId.firstValue, postHeaders.firstValue["X-Reference-Id"])

        // Get
        val getHeaders = argumentCaptor<Map<String, String?>>()
        verify(http).get(
            eq(referenceId.firstValue),
            eq(config.environment.baseUrl + "/collection/v1_0/requesttopay/${referenceId.firstValue}"),
            eq(MTNRequestToPayResponse::class.java),
            getHeaders.capture(),
        )

        assertEquals("application/json", getHeaders.firstValue["Content-Type"])
        assertEquals("Bearer $accessToken", getHeaders.firstValue["Authorization"])
        assertEquals(config.callbackUrl, getHeaders.firstValue["X-Callback-Url"])
        assertEquals(config.environment.name.lowercase(), getHeaders.firstValue["X-Target-Environment"])
        assertEquals(config.subscriptionKey, getHeaders.firstValue["Ocp-Apim-Subscription-Key"])
        assertEquals(null, getHeaders.firstValue["X-Reference-Id"])
    }

    @Test
    fun `payment - PENDING`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "PENDING",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreatePaymentRequest(Fixtures.NUMBER_PENDING)
        val response = gateway.createPayment(request)

        // THEN
        val referenceId = argumentCaptor<String>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/collection/v1_0/requesttopay"),
            anyOrNull(),
            eq(Any::class.java),
            anyOrNull(),
        )

        assertEquals(Status.PENDING, response.status)
        assertEquals(referenceId.firstValue, response.transactionId)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
    }

    @Test
    fun `payment - FAILED`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "FAILED",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            reason = "PAYER_LIMIT_REACHED",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreatePaymentRequest(Fixtures.NUMBER_FAILED)
        val ex = assertThrows<PaymentException> {
            gateway.createPayment(request)
        }

        // THEN
        val referenceId = argumentCaptor<String>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/collection/v1_0/requesttopay"),
            anyOrNull(),
            eq(Any::class.java),
            anyOrNull(),
        )

        assertEquals(ErrorCode.PAYER_LIMIT_REACHED, ex.error.code)
        assertEquals(mtnResponse.reason, ex.error.supplierErrorCode)
        assertEquals(referenceId.firstValue, ex.error.transactionId)
    }

    @Test
    fun `get payment - PENDING`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "PENDING",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val response = gateway.getPayment(externalId)

        assertEquals(Status.PENDING, response.status)
        assertEquals(mtnResponse.amount, response.amount.value.toString())
        assertEquals(mtnResponse.currency, response.amount.currency)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
        assertEquals(Money(), response.fees)
        assertEquals(mtnResponse.payeeNote, response.description)
        assertEquals(mtnResponse.payerMessage, response.payerMessage)
        assertEquals(mtnResponse.payer.partyId, response.payer.phoneNumber)
    }

    @Test
    fun `get payment - SUCCESS`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "SUCCESSFUL",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val response = gateway.getPayment(externalId)

        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(mtnResponse.amount, response.amount.value.toString())
        assertEquals(mtnResponse.currency, response.amount.currency)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
        assertEquals(Money(), response.fees)
        assertEquals(mtnResponse.payeeNote, response.description)
        assertEquals(mtnResponse.payerMessage, response.payerMessage)
        assertEquals(mtnResponse.payer.partyId, response.payer.phoneNumber)
    }

    @Test
    fun `get payment - FAILED`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNRequestToPayResponse(
            status = "FAILED",
            externalId = externalId,
            payer = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
            reason = MTNErrorCode.EXPIRED.name,
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val ex = assertThrows<PaymentException> {
            gateway.getPayment(externalId)
        }

        assertEquals(ErrorCode.EXPIRED, ex.error.code)
        assertEquals(mtnResponse.reason, ex.error.supplierErrorCode)
        assertEquals(externalId, ex.error.transactionId)
    }

    @Test
    fun `create transfer - SUCCESS`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "SUCCESSFUL",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            payeeNote = "Hello",
            payerMessage = "Yo",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreateTransferRequest(Fixtures.NUMBER_SUCCESS)
        val response = gateway.createTransfer(request)

        // THEN

        // Submit
        val referenceId = argumentCaptor<String>()
        val payload = argumentCaptor<MTNTransferRequest>()
        val postHeaders = argumentCaptor<Map<String, String?>>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/disbursement/v1_0/transfer"),
            payload.capture(),
            eq(Any::class.java),
            postHeaders.capture(),
        )

        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(referenceId.firstValue, response.transactionId)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)

        assertEquals(request.amount.value.toString(), payload.firstValue.amount)
        assertEquals("EUR", payload.firstValue.currency)
        assertEquals(Fixtures.NUMBER_SUCCESS.substring(1), payload.firstValue.payee.partyId)
        assertEquals("MSISDN", payload.firstValue.payee.partyIdType)
        assertEquals(request.externalId, payload.firstValue.externalId)
        assertEquals(request.description, payload.firstValue.payeeNote)
        assertEquals(request.payerMessage, payload.firstValue.payerMessage)

        assertEquals("application/json", postHeaders.firstValue["Content-Type"])
        assertEquals("Bearer $accessToken", postHeaders.firstValue["Authorization"])
        assertEquals(config.callbackUrl, postHeaders.firstValue["X-Callback-Url"])
        assertEquals(config.environment.name.lowercase(), postHeaders.firstValue["X-Target-Environment"])
        assertEquals(config.subscriptionKey, postHeaders.firstValue["Ocp-Apim-Subscription-Key"])
        assertEquals(referenceId.firstValue, postHeaders.firstValue["X-Reference-Id"])

        // Get
        val getHeaders = argumentCaptor<Map<String, String?>>()
        verify(http).get(
            eq(referenceId.firstValue),
            eq(config.environment.baseUrl + "/disbursement/v1_0/transfer/${referenceId.firstValue}"),
            eq(MTNTransferResponse::class.java),
            getHeaders.capture(),
        )

        assertEquals("application/json", getHeaders.firstValue["Content-Type"])
        assertEquals("Bearer $accessToken", getHeaders.firstValue["Authorization"])
        assertEquals(config.callbackUrl, getHeaders.firstValue["X-Callback-Url"])
        assertEquals(config.environment.name.lowercase(), getHeaders.firstValue["X-Target-Environment"])
        assertEquals(config.subscriptionKey, getHeaders.firstValue["Ocp-Apim-Subscription-Key"])
        assertEquals(referenceId.firstValue, getHeaders.firstValue["X-Reference-Id"])
    }

    @Test
    fun `create transfer - PENDING`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "PENDING",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            payeeNote = "Hello",
            payerMessage = "Yo",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreateTransferRequest(Fixtures.NUMBER_SUCCESS)
        val response = gateway.createTransfer(request)

        // THEN

        // Submit
        val referenceId = argumentCaptor<String>()
        val payload = argumentCaptor<MTNTransferRequest>()
        val postHeaders = argumentCaptor<Map<String, String?>>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/disbursement/v1_0/transfer"),
            payload.capture(),
            eq(Any::class.java),
            postHeaders.capture(),
        )

        assertEquals(Status.PENDING, response.status)
        assertEquals(referenceId.firstValue, response.transactionId)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
    }

    @Test
    fun `create transfer - FAILED`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "FAILED",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            payeeNote = "Hello",
            payerMessage = "Yo",
            reason = MTNErrorCode.NOT_ENOUGH_FUNDS.name,
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val request = createCreateTransferRequest(Fixtures.NUMBER_FAILED)
        val ex = assertThrows<PaymentException> {
            gateway.createTransfer(request)
        }

        // THEN
        val referenceId = argumentCaptor<String>()
        verify(http).post(
            referenceId.capture(),
            eq(config.environment.baseUrl + "/disbursement/v1_0/transfer"),
            anyOrNull(),
            eq(Any::class.java),
            anyOrNull(),
        )

        assertEquals(ErrorCode.NOT_ENOUGH_FUNDS, ex.error.code)
        assertEquals(mtnResponse.reason, ex.error.supplierErrorCode)
        assertEquals(referenceId.firstValue, ex.error.transactionId)
    }

    @Test
    fun `get transfer - SUCCESS`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "SUCCESSFUL",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val response = gateway.getTransfer(externalId)

        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(mtnResponse.amount, response.amount.value.toString())
        assertEquals(mtnResponse.currency, response.amount.currency)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
        assertEquals(Money(), response.fees)
        assertEquals(mtnResponse.payeeNote, response.description)
        assertEquals(mtnResponse.payerMessage, response.payerMessage)
        assertEquals(mtnResponse.payee.partyId, response.payee.phoneNumber)
    }

    @Test
    fun `get transfer - PENDING`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "PENDING",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val response = gateway.getTransfer(externalId)

        assertEquals(Status.PENDING, response.status)
        assertEquals(mtnResponse.amount, response.amount.value.toString())
        assertEquals(mtnResponse.currency, response.amount.currency)
        assertEquals(mtnResponse.financialTransactionId, response.financialTransactionId)
        assertEquals(Money(), response.fees)
        assertEquals(mtnResponse.payeeNote, response.description)
        assertEquals(mtnResponse.payerMessage, response.payerMessage)
        assertEquals(mtnResponse.payee.partyId, response.payee.phoneNumber)
    }

    @Test
    fun `get transfer - FAILED`() {
        // GIVEN
        val externalId = "payment-external-id"
        val mtnResponse = MTNTransferResponse(
            status = "FAILED",
            externalId = externalId,
            payee = MTNParty(
                partyId = payer.phoneNumber,
            ),
            financialTransactionId = "financial-transaction-id",
            amount = "1000.0",
            currency = "EUR",
            payeeNote = "Yo man",
            payerMessage = "How are you?",
            reason = MTNErrorCode.EXPIRED.name,
        )
        doReturn(mtnResponse).whenever(http).get(anyOrNull(), anyOrNull(), anyOrNull<Class<*>>(), anyOrNull())

        // WHEN
        val ex = assertThrows<PaymentException> {
            gateway.getTransfer(externalId)
        }

        // THEN
        assertEquals(ErrorCode.EXPIRED, ex.error.code)
        assertEquals(mtnResponse.reason, ex.error.supplierErrorCode)
        assertEquals(externalId, ex.error.transactionId)
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
