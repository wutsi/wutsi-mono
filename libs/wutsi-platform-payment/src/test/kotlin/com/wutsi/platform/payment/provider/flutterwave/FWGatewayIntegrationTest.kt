package com.wutsi.platform.payment.provider.flutterwave

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.BankAccount
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.CreateTransferRequest
import com.wutsi.platform.payment.model.CreditCard
import com.wutsi.platform.payment.model.Party
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.http.HttpClient
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class FWGatewayIntegrationTest {
    private val secretKey = "FLWSECK_TEST-b4cb2c97ac5127c3bd06995c0ce1032a-X"
    private val encryptionKey = "FLWSECK_TESTe366bc384143"
    private lateinit var http: Http
    private lateinit var gateway: FWGateway

    @BeforeEach
    fun setUp() {
        val om = ObjectMapper()
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        http = Http(
            HttpClient.newHttpClient(),
            om,
        )
        gateway = FWGateway(http, secretKey, true, FWEncryptor(ObjectMapper(), encryptionKey))
    }

    @Test
    fun `MobileMoney - payment`() {
        // WHEN
        val request = createMobileMoneyPaymentRequest("+23700000020")
        val response = gateway.createPayment(request)
        assertEquals(Status.PENDING, response.status)
        assertEquals(375.0, response.fees.value)
        assertNotNull(response.transactionId)
        assertNotNull(response.financialTransactionId)

        println("Fetching details...")
        val response2 = gateway.getPayment(response.transactionId)
        assertEquals(request.amount, response2.amount)
        assertEquals(response.fees, response2.fees)
        assertEquals(response.status, response2.status)
        assertEquals(request.externalId, response2.externalId)
        assertEquals(request.payer.email, response2.payer.email)
        assertEquals(request.payer.fullName, response2.payer.fullName)
        assertEquals(request.payer.phoneNumber.substring(1), response2.payer.phoneNumber)
        assertNull(response2.payer.country)
    }

    @Test
    fun `Card - payment`() {
        // WHEN
        val request = createCreditCardPaymentRequest("4751763236699647", "564", 9, 2035)
        val response = gateway.createPayment(request)
        assertEquals(Status.SUCCESSFUL, response.status)
        assertEquals(646.0, response.fees.value)
        assertNotNull(response.transactionId)
        assertNotNull(response.financialTransactionId)

        println("Fetching details...")
        val response2 = gateway.getPayment(response.transactionId)
        assertEquals(request.amount, response2.amount)
        assertEquals(response.fees, response2.fees)
        assertEquals(response.status, response2.status)
        assertEquals(request.externalId, response2.externalId)
        assertEquals(request.payer.email, response2.payer.email)
        assertEquals(request.creditCard?.owner, response2.payer.fullName)
        assertEquals(request.payer.phoneNumber.substring(1), response2.payer.phoneNumber)
        assertNull(response2.payer.country)
    }

    @Test
    fun `MobileMoney - transfer`() {
        // TRANSFER
        println("Transfer...")
        val request = createMobileMoneyTransferRequest("+23700000020")
        val response = gateway.createTransfer(request)
        assertEquals(Status.PENDING, response.status)
        assertEquals(500.0, response.fees.value)
        assertNotNull(response.transactionId)
        assertNull(response.financialTransactionId)

        println("Fetching details...")
        val response2 = gateway.getTransfer(response.transactionId)
        assertEquals(request.amount, response2.amount)
        assertEquals(response.fees, response2.fees)
        assertEquals(response.status, response2.status)
        assertEquals(request.externalId, response2.externalId)
        assertNull(response2.payee.email)
        assertEquals(request.payee.fullName, response2.payee.fullName)
        assertEquals(request.payee.country, response2.payee.country)
        assertEquals(request.payee.phoneNumber.substring(1), response2.payee.phoneNumber)
    }

    @Test
    fun `Bank - transfer`() {
        // TRANSFER
        println("Transfer...")
        val request = createBankTransferRequest("0690000031")
        val response = gateway.createTransfer(request)
        assertEquals(Status.PENDING, response.status)
        assertEquals(45.0, response.fees.value)
        assertNotNull(response.transactionId)
        assertNull(response.financialTransactionId)

        println("Fetching details...")
        val response2 = gateway.getTransfer(response.transactionId)
        assertEquals(request.amount, response2.amount)
        assertEquals(response.fees, response2.fees)
        assertEquals(response.status, response2.status)
        assertEquals(request.externalId, response2.externalId)
        assertNull(response2.payee.email)
        assertEquals(request.payee.fullName, response2.payee.fullName)
    }

    @Test
    fun healtcheck() {
        gateway.health()
    }

    private fun createMobileMoneyTransferRequest(phoneNumber: String) = CreateTransferRequest(
        payee = Party(
            fullName = "Ray Sponsible",
            phoneNumber = phoneNumber,
            email = "",
        ),
        amount = Money(
            value = 25000.0,
            currency = "XAF",
        ),
        payerMessage = "Hello wold",
        externalId = UUID.randomUUID().toString(),
        description = "Sample product",
    )

    private fun createMobileMoneyPaymentRequest(phoneNumber: String) = CreatePaymentRequest(
        payer = Party(
            fullName = "Ray Sponsible",
            phoneNumber = phoneNumber,
            email = "ray.sponsible@yahoo.com",
            country = "CM",
        ),
        amount = Money(
            value = 15000.0,
            currency = "XAF",
        ),
        payerMessage = "Hello wold",
        externalId = UUID.randomUUID().toString(),
        description = "Sample product",
    )

    private fun createCreditCardPaymentRequest(number: String, cvv: String, expiryMonth: Int, expiryYear: Int) =
        CreatePaymentRequest(
            payer = Party(
                fullName = "Ray Sponsible",
                phoneNumber = "+237670000011",
                email = "ray.sponsible@yahoo.com",
                country = "CM",
            ),
            amount = Money(
                value = 17000.0,
                currency = "XAF",
            ),
            creditCard = CreditCard( // See https://developer.flutterwave.com/docs/integration-guides/testing-helpers/
                number = number,
                cvv = cvv,
                expiryMonth = expiryMonth,
                expiryYear = expiryYear,
                "RAY SPONSIBLE",
            ),
            payerMessage = "Hello wold",
            externalId = UUID.randomUUID().toString(),
            description = "Sample product",
        )

    private fun createBankTransferRequest(accoutNumber: String = "0690000031") = CreateTransferRequest(
        sender = Party(
            fullName = "Roger Milla",
            phoneNumber = "+237670000011",
            email = "roger.milla@gmail.com",
            country = "CM",
        ),
        payee = Party(
            fullName = "Ray Sponsible",
            phoneNumber = "-",
            email = "ray.sponsible@gmail.com",
            country = "CM",
        ),
        amount = Money(
            value = 25000.0,
            currency = "XAF",
        ),
        bankAccount = BankAccount(
            number = accoutNumber,
            bankName = "AFRILAND FIRSTBANK",
            country = "CM",
            owner = "RAY SPONSIBLE",
        ),
        payerMessage = "Hello wold",
        externalId = UUID.randomUUID().toString(),
        description = "Sample product",
    )
}
