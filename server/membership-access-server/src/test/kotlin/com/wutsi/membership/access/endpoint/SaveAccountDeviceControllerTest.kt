package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.dao.DeviceRepository
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SaveAccountDeviceController.sql"])
public class SaveAccountDeviceControllerTest {
    @LocalServerPort
    public val port: Int = 0

    protected val rest = RestTemplate()

    @Autowired
    private lateinit var dao: DeviceRepository

    @Test
    public fun update() {
        val request = SaveAccountDeviceRequest(
            token = "----- this is --tokern---",
            type = "Phone",
            model = "Android",
            osName = "Android",
            osVersion = "1.10.12.10120",
        )
        val response = rest.postForEntity(url(100), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val device = dao.findById(100).get()
        assertEquals(request.token, device.token)
        assertEquals(request.type, device.type)
        assertEquals(request.osVersion, device.osVersion)
        assertEquals(request.osName, device.osName)
        assertEquals(request.model, device.model)
        assertNotNull(device.created)
        assertNotNull(device.updated)
    }

    @Test
    public fun create() {
        val request = SaveAccountDeviceRequest(
            token = "----- this is --tokern---",
            type = "Phone",
            model = "Android",
            osName = "Android",
            osVersion = "1.10.12.10120",
        )
        val response = rest.postForEntity(url(200), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val device = dao.findById(200).get()
        assertEquals(request.token, device.token)
        assertEquals(request.type, device.type)
        assertEquals(request.osVersion, device.osVersion)
        assertEquals(request.osName, device.osName)
        assertEquals(request.model, device.model)
        assertNotNull(device.created)
        assertNotNull(device.updated)
    }

    fun url(id: Long) = "http://localhost:$port/v1/accounts/$id/device"
}
