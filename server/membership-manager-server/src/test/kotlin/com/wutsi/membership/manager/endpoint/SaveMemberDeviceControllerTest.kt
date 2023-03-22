package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.enums.DeviceType
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.manager.dto.SaveDeviceRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SaveMemberDeviceControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        val request = SaveDeviceRequest(
            token = "111",
            type = DeviceType.MOBILE.name,
            osName = "Android",
            osVersion = "1.3.232",
            model = "Tablet",
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipAccess).saveAccountDevice(
            id = ACCOUNT_ID,
            request = SaveAccountDeviceRequest(
                token = request.token,
                type = request.type,
                osName = request.osName,
                osVersion = request.osVersion,
                model = request.model,
            ),
        )
    }

    private fun url() = "http://localhost:$port/v1/members/device"
}
