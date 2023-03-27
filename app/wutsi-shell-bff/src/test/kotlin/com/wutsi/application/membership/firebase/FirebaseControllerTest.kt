package com.wutsi.application.membership.firebase

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.firebase.dto.SubmitTokenRequest
import com.wutsi.enums.DeviceType
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

internal class FirebaseControllerTest : ClientHttpRequestInterceptor, AbstractSecuredEndpointTest() {
    companion object {
        const val OS = "Android"
        const val VERSION = "1.11.1"
    }

    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/firebase/token"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest.interceptors.add(this)
    }

    @Test
    fun token() {
        val request = SubmitTokenRequest(token = "XXXX")
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipManagerApi).saveMemberDevice(
            request = SaveDeviceRequest(
                token = request.token,
                osName = OS,
                osVersion = VERSION,
                type = DeviceType.MOBILE.name,
            ),
        )
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers["X-OS"] = OS
        request.headers["X-OS-Version"] = VERSION
        return execution.execute(request, body)
    }
}
