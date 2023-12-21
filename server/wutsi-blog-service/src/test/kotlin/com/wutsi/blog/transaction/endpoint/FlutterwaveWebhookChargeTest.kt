package com.wutsi.blog.transaction.endpoint

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.Fixtures.createFWWebhookRequest
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.AfterEach
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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/FlutterwaveWebhookCharge.sql"])
class FlutterwaveWebhookChargeTest : ClientHttpRequestInterceptor {
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

    @Value("\${wutsi.platform.payment.flutterwave.secret-hash}")
    private lateinit var secretHash: String

    @Autowired
    private lateinit var productDao: ProductRepository

    @Autowired
    private lateinit var storeDao: StoreRepository

    @Value("\${spring.mail.port}")
    private lateinit var smtpPort: String

    private lateinit var smtp: GreenMail

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
        smtp = GreenMail(ServerSetup.SMTP.port(smtpPort.toInt()))
        smtp.setUser("wutsi", "secret")
        smtp.start()

        rest.restTemplate.interceptors = listOf(this)

        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @AfterEach
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
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
        val tx = dao.findById(transactionId).get()
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals(response.fees.value.toLong(), tx.gatewayFees)
        assertEquals(2000, tx.fees)
        assertEquals(8000, tx.net)
        assertEquals(10000, tx.amount)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)
        assertTrue(tx.lastModificationDateTime.after(now))

        Thread.sleep(15000)
        val wallet = walletDao.findById("1").get()
        assertEquals(11500, wallet.balance)
        assertEquals(2, wallet.chargeCount)
        assertTrue(wallet.lastModificationDateTime.after(now))

        val product = productDao.findById(101L).get()
        assertEquals(12500, product.totalSales)
        assertEquals(2, product.orderCount)

        val store = storeDao.findById("100").get()
        assertEquals(11500, store.totalSales)
        assertEquals(2, store.orderCount)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        deliveredTo(tx.email!!, messages)
        println("------------------------------")
        println(messages[0].content.toString())
    }

    fun deliveredTo(email: String, messages: Array<MimeMessage>): Boolean =
        messages.find { message ->
            message.getRecipients(Message.RecipientType.TO).find {
                it.toString().contains(email)
            } != null
        } != null
}
