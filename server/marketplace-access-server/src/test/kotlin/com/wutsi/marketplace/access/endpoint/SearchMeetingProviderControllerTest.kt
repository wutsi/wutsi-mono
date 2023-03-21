package com.wutsi.marketplace.access.endpoint

import com.wutsi.enums.MeetingProviderType
import com.wutsi.marketplace.access.dto.SearchMeetingProviderResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchMeetingProviderControllerTest {
    @LocalServerPort
    public val port: Int = 0

    protected val rest = RestTemplate()

    @Test
    fun invoke() {
        val response = rest.postForEntity(url(), null, SearchMeetingProviderResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val providers = response.body!!.meetingProviders
        assertEquals(2, providers.size)

        assertEquals(1000L, providers[0].id)
        assertEquals("Zoom", providers[0].name)
        assertEquals(MeetingProviderType.ZOOM.name, providers[0].type)
        assertEquals(
            "https://prod-wutsi.s3.amazonaws.com/static/wutsi-assets/images/meeting-providers/zoom.png",
            providers[0].logoUrl,
        )

        assertEquals(1001L, providers[1].id)
        assertEquals("Meet", providers[1].name)
        assertEquals(MeetingProviderType.MEET.name, providers[1].type)
        assertEquals(
            "https://prod-wutsi.s3.amazonaws.com/static/wutsi-assets/images/meeting-providers/meet.png",
            providers[1].logoUrl,
        )
    }

    private fun url() = "http://localhost:$port/v1/meeting-providers/search"
}
