package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.util.DateUtils
import com.wutsi.event.store.EventStore
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.core.Money
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.GetPaymentResponse
import com.wutsi.platform.payment.model.Party
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
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
@Sql(value = ["/db/clean.sql", "/db/transaction/ReconciliateDonationCommand.sql"])
class ReconciliateDonationCommandTest : ClientHttpRequestInterceptor {
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

    private var accessToken: String? = "session-ray"

    @Value("\${wutsi.application.transaction.donation.fees-percentage}")
    private lateinit var donationFeesPercent: java.lang.Double

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
    fun noLocalTransaction() {
        val walletId = "1"
        val balance = walletDao.findById(walletId).get().balance

        val gatewayTransactionId = UUID.randomUUID().toString()
        val response = GetPaymentResponse(
            walletId = null,
            amount = Money(10000.0, "XAF"),
            creationDateTime = DateUtils.addDays(Date(), -1),
            status = Status.SUCCESSFUL,
            financialTransactionId = "1212",
            payer = Party(
                email = "roger.milla@gmail.com",
                fullName = "Roger Milla",
                phoneNumber = "+237971111111",
            ),
            externalId = "11111111111111",
            fees = Money(350.0, "XAF"),
        )
        doReturn(response).whenever(flutterwave).getPayment(any())

        // WHEN
        Thread.sleep(1000)
        val result = rest.getForEntity(
            "/v1/transactions/commands/reconciliate-donation?gateway-transaction-id=$gatewayTransactionId&wallet-id=$walletId&gateway-type=FLUTTERWAVE",
            Any::class.java,
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = dao.findById(response.externalId).get()
        val fees = (donationFeesPercent.toDouble() * response.amount.value).toLong()
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(Status.SUCCESSFUL, tx.status)
        assertEquals(36, tx.idempotencyKey.length)
        assertNull(tx.user?.id)
        assertEquals(walletId, tx.wallet.id)
        assertEquals(response.payer.email, tx.email)
        assertEquals(response.description, tx.description)
        assertEquals(false, tx.anonymous)
        assertEquals(response.payer.fullName, tx.paymentMethodOwner)
        assertEquals(PaymentMethodType.MOBILE_MONEY, tx.paymentMethodType)
        assertEquals(response.payer.phoneNumber, tx.paymentMethodNumber)
        assertEquals(response.fees.value.toLong(), tx.gatewayFees)
        assertEquals(response.fees.value.toLong(), tx.gatewayFees)
        assertEquals(fees, tx.fees)
        assertEquals(response.amount.value.toLong() - fees, tx.net)
        assertEquals(response.amount.currency, tx.currency)
        assertEquals(gatewayTransactionId, tx.gatewayTransactionId)
        assertEquals(GatewayType.FLUTTERWAVE, tx.gatewayType)
        assertEquals(response.creationDateTime!!.time / 100000, tx.creationDateTime.time / 100000)
        assertNull(tx.errorCode)
        assertNull(tx.errorMessage)
        assertNull(tx.supplierErrorCode)

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_RECONCILIATED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val wallet = walletDao.findById(walletId).get()
        assertEquals(balance + tx.net, wallet.balance)
    }

    @Test
    fun withLocalTransaction() {
        val now = Date()
        val walletId = "1"
        val gatewayTransactionId = UUID.randomUUID().toString()
        val response = GetPaymentResponse(
            walletId = null,
            amount = Money(10000.0, "XAF"),
            creationDateTime = DateUtils.addDays(Date(), -1),
            status = Status.SUCCESSFUL,
            financialTransactionId = "1212",
            payer = Party(
                email = "ray.sponsible@gmail.com",
                fullName = "Ray Sponsible",
                phoneNumber = "+237971111111",
            ),
            externalId = "100",
            description = "This is the description",
            fees = Money(350.0, "XAF"),
        )
        doReturn(response).whenever(flutterwave).getPayment(any())

        // WHEN
        Thread.sleep(1000)
        val result = rest.getForEntity(
            "/v1/transactions/commands/reconciliate-donation?gateway-transaction-id=$gatewayTransactionId&wallet-id=$walletId&gateway-type=FLUTTERWAVE",
            Any::class.java,
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val tx = dao.findById(response.externalId).get()
        assertTrue(tx.lastModificationDateTime.before(now))

        val events = eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = tx.id,
            type = EventType.TRANSACTION_RECONCILIATED_EVENT,
        )
        assertFalse(events.isNotEmpty())
    }
}
