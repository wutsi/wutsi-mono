package com.wutsi.platform.payment.provider.paypal

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CreatePaymentRequest
import com.wutsi.platform.payment.model.Party
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.http.HttpClient
import kotlin.test.assertEquals

class PaypalIntegrationTest {
    companion object {
        const val CLIENT_ID = "AVd6GRKZ9A1GQR7UcxadqfC0srM17ksKLwABcblGV72xAjm963GcpqPjCYT7Fd8pbWg8fUD3Bef16SOK"
        const val SECRET_KEY = "EDAAywKD2sKieducUAFXKna-MoemX2ur3JtEwNBBMhe3JHDMOBYCjdY_4l327Dch8OYW4WBrOyuLG3y2"
    }

    private val om = ObjectMapper()
    private val http = Http(HttpClient.newHttpClient(), om)
    private val paypal = Paypal(
        clientId = CLIENT_ID,
        secretKey = SECRET_KEY,
        http = http,
        testMode = true
    )

    @BeforeEach
    fun setUp() {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    fun createPayment() {
        // Create
        val req = CreatePaymentRequest(
            payer = Party(
                id = "111",
                fullName = "John Smith",
                email = "roger.milla@gmail.com",
                country = "CM",
                phoneNumber = ""
            ),
            description = "Sample payment",
            externalId = "11112222",
            amount = Money(1000.0, "USD"),
            walletId = "wallet:12334",
            deviceId = "1210:12012:11:xxx",
            payerMessage = "Test"
        )
        val resp = paypal.createPayment(req)
        assertEquals(Status.PENDING, resp.status)

        // Get
        val respp = paypal.getPayment(resp.transactionId)
        assertEquals(resp.status, respp.status)
        assertEquals(req.externalId, respp.externalId)
        assertEquals(req.description, respp.description)
        assertEquals(req.amount, respp.amount)
    }
//
//    @Test
//    fun orderFailed() {
//        assertThrows<PaymentException> {
//            paypal.createOrder(
//                PPCreateOrderRequest(
//                    intent = "CAPTURE",
//                    purchase_units = listOf(
//                        PPPurchaseUnit(
//                            reference_id = "111",
//                            description = "This is a sample product",
//                            amount = PPMoney(10.0, "XXXX"),
//                        )
//                    )
//                )
//            )
//        }
//    }
}