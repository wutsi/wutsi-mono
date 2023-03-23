package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.MeetingProviderType
import com.wutsi.marketplace.access.dto.SearchMeetingProviderResponse
import com.wutsi.marketplace.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchMeetingProviderControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    private fun url() = "http://localhost:$port/v1/meeting-providers/search"

    @Test
    public fun invoke() {
        // GIVEN
        val providers = listOf(
            Fixtures.createMeetingProviderSummary(1, MeetingProviderType.ZOOM),
            Fixtures.createMeetingProviderSummary(2, MeetingProviderType.MEET),
        )
        doReturn(SearchMeetingProviderResponse(providers)).whenever(marketplaceAccessApi).searchMeetingProvider()

        // WHEN
        val response =
            rest.postForEntity(url(), null, com.wutsi.marketplace.manager.dto.SearchMeetingProviderResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).searchMeetingProvider()

        assertEquals(providers.size, response.body!!.meetingProviders.size)

        verify(eventStream, never()).publish(any(), any())
    }
}
