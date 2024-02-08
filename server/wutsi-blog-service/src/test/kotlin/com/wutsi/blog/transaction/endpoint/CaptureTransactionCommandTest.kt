package com.wutsi.blog.transaction.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.CaptureTransactionCommand
import com.wutsi.blog.transaction.dto.CaptureTransactionResponse
import com.wutsi.platform.payment.GatewayType
import com.wutsi.platform.payment.PaymentException
import com.wutsi.platform.payment.core.Error
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import com.wutsi.platform.payment.model.CapturePaymentResponse
import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/transaction/CaptureTransactionCommand.sql"])
class CaptureTransactionCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: TransactionRepository

    @MockBean
    private lateinit var flutterwave: Flutterwave

    @BeforeEach
    fun setUp() {
        doReturn(GatewayType.FLUTTERWAVE).whenever(flutterwave).getType()
    }

    @Test
    fun pending() {
        // GIVEN
        val response = CapturePaymentResponse(
            transactionId = UUID.randomUUID().toString(),
            status = Status.SUCCESSFUL,
        )
        doReturn(response).whenever(flutterwave).capturePayment(any())

        // WHEN
        val command = CaptureTransactionCommand(
            transactionId = "100"
        )
        val result =
            rest.postForEntity(
                "/v1/transactions/commands/capture-transaction",
                command,
                CaptureTransactionResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.PENDING.name, result.body!!.status)
        assertNull(result.body!!.errorCode)
        assertNull(result.body!!.errorMessage)

        verify(flutterwave).capturePayment("100100")

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(Status.PENDING, tx.status)
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
        doThrow(ex).whenever(flutterwave).capturePayment(any())

        // WHEN
        val command = CaptureTransactionCommand(
            transactionId = "100"
        )
        val result =
            rest.postForEntity(
                "/v1/transactions/commands/capture-transaction",
                command,
                CaptureTransactionResponse::class.java
            )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(Status.FAILED.name, result.body!!.status)
        assertEquals(ex.error.code.name, result.body!!.errorCode)
        assertEquals(ex.error.message, result.body!!.errorMessage)

        val tx = dao.findById(result.body!!.transactionId).get()
        assertEquals(Status.FAILED, tx.status)
        assertEquals(ex.error.transactionId, tx.gatewayTransactionId)
        assertEquals(ex.error.code.name, tx.errorCode)
        assertEquals(ex.error.message, tx.errorMessage)
        assertEquals(ex.error.supplierErrorCode, tx.supplierErrorCode)
    }
}
