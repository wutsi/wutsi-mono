package com.wutsi.tracking.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Pipeline
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PushControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @MockBean
    private lateinit var pipeline: Pipeline

    private fun url() = "http://localhost:$port/v1/tracks"

    @Test
    fun invoke() {
        // WHEN
        val request = Fixtures.createPushTrackRequest()
        val response = rest.postForEntity(url(), request, PushTrackResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(pipeline).filter(
            TrackEntity(
                time = request.time,
                ua = request.ua,
                correlationId = request.correlationId,
                event = request.event,
                productId = request.productId,
                page = request.page,
                value = request.value,
                revenue = request.revenue,
                long = request.long,
                lat = request.lat,
                ip = request.ip,
                deviceId = request.deviceId,
                accountId = request.accountId,
                merchantId = request.merchantId,
                referrer = request.referrer,
                url = request.url,
                businessId = request.businessId,
            ),
        )
    }
}
