package com.wutsi.security.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import com.wutsi.security.manager.error.ErrorURN
import com.wutsi.security.manager.service.OtpService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOtpControllerTest {
    @LocalServerPort
    val port: Int = 0

    protected lateinit var rest: RestTemplate

    @Autowired
    private lateinit var dao: com.wutsi.security.manager.dao.OtpRepository

    @MockBean
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    private lateinit var messaging: MessagingService

    @Autowired
    private lateinit var service: OtpService

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()

        messaging = mock()
        service.testAddresses = mutableListOf()
    }

    @Test
    fun createOTPViaSMS() {
        // GIVEN
        val now = System.currentTimeMillis()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.SMS)

        // WHEN
        val request = CreateOTPRequest(
            type = MessagingType.SMS.name,
            address = "+23799505678",
        )
        val response = rest.postForEntity(url(), request, CreateOTPResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val token = response.body?.token
        assertNotNull(token)

        val otp = dao.findById(token).get()
        assertEquals(token, otp.token)
        assertEquals(6, otp.code.length)
        assertTrue(otp.expires - now >= OtpService.OTP_TTL_MILLIS)
        assertEquals(request.address, otp.address)

        val msg = argumentCaptor<Message>()
        verify(messaging).send(msg.capture())
        assertEquals(request.address, msg.firstValue.recipient.phoneNumber)
        assertEquals("[Wutsi] Verification code", msg.firstValue.subject)
        assertEquals("Your verification code: ${otp.code}", msg.firstValue.body)
    }

    @Test
    fun createOTPViaPushNotification() {
        // GIVEN
        val now = System.currentTimeMillis()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.PUSH_NOTIFICATION)

        // WHEN
        val request = CreateOTPRequest(
            type = MessagingType.PUSH_NOTIFICATION.name,
            address = UUID.randomUUID().toString(),
        )
        val response = rest.postForEntity(url(), request, CreateOTPResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val token = response.body?.token
        assertNotNull(token)

        val otp = dao.findById(token).get()
        assertEquals(token, otp.token)
        assertEquals(6, otp.code.length)
        assertTrue(otp.expires - now >= OtpService.OTP_TTL_MILLIS)

        val msg = argumentCaptor<Message>()
        verify(messaging).send(msg.capture())
        assertEquals(request.address, msg.firstValue.recipient.deviceToken)
        assertEquals("[Wutsi] Verification code", msg.firstValue.subject)
        assertEquals("Your verification code: ${otp.code}", msg.firstValue.body)
    }

    @Test
    fun createOTPViaEmail() {
        // GIVEN
        val now = System.currentTimeMillis()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.EMAIL)

        // WHEN
        val request = CreateOTPRequest(
            type = MessagingType.EMAIL.name,
            address = "roger.milla@gmail.com",
        )
        val response = rest.postForEntity(url(), request, CreateOTPResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val token = response.body?.token
        assertNotNull(token)

        val otp = dao.findById(token).get()
        assertEquals(token, otp.token)
        assertEquals(6, otp.code.length)
        assertTrue(otp.expires - now >= OtpService.OTP_TTL_MILLIS)
        assertEquals(request.address, otp.address)

        val msg = argumentCaptor<Message>()
        verify(messaging).send(msg.capture())
        assertEquals(request.address, msg.firstValue.recipient.phoneNumber)
        assertEquals("[Wutsi] Verification code", msg.firstValue.subject)
        assertEquals("Your verification code: ${otp.code}", msg.firstValue.body)
    }

    @Test
    fun `send OTP Via Whatsapp`() {
        // GIVEN
        val now = System.currentTimeMillis()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.WHATSTAPP)

        // WHEN
        val request = CreateOTPRequest(
            type = MessagingType.WHATSTAPP.name,
            address = "+23799505678",
        )
        val response = rest.postForEntity(url(), request, CreateOTPResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val token = response.body?.token
        assertNotNull(token)

        val otp = dao.findById(token).get()
        assertEquals(token, otp.token)
        assertEquals(6, otp.code.length)
        assertTrue(otp.expires - now >= OtpService.OTP_TTL_MILLIS)
        assertEquals(request.address, otp.address)

        val msg = argumentCaptor<Message>()
        verify(messaging).send(msg.capture())
        assertEquals(request.address, msg.firstValue.recipient.phoneNumber)
        assertEquals("[Wutsi] Verification code", msg.firstValue.subject)
        assertEquals("Your verification code: ${otp.code}", msg.firstValue.body)
    }

    @Test
    fun `never create OTP with bad address`() {
        // WHEN
        val request = CreateOTPRequest(
            type = "bad-address-type",
            address = "+23799505678",
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, CreateOTPResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.OTP_ADDRESS_TYPE_NOT_VALID.urn, response.error.code)
    }

    @Test
    fun `never send OTP to test address`() {
        // WHEN
        val now = System.currentTimeMillis()
        val request = CreateOTPRequest(
            type = "bad-address-type",
            address = "+23799505678",
        )
        service.testAddresses = mutableListOf(request.address)
        val response = rest.postForEntity(url(), request, CreateOTPResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val token = response.body?.token
        assertNotNull(token)

        val otp = dao.findById(token).get()
        assertEquals(token, otp.token)
        assertEquals(6, otp.code.length)
        assertTrue(otp.expires - now >= OtpService.OTP_TTL_MILLIS)
        assertEquals(request.address, otp.address)

        verify(messaging, never()).send(any())
    }

    private fun url() = "http://localhost:$port/v1/otp"
}
