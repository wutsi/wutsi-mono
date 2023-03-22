package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dto.SearchPlaceResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchPlaceControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        // GIVEN
        val place1 = Fixtures.createPlaceSummary()
        val place2 = Fixtures.createPlaceSummary()
        doReturn(SearchPlaceResponse(listOf(place1, place2))).whenever(membershipAccess).searchPlace(any())

        // WHEN
        val request = SearchPlaceRequest(
            type = PlaceType.CITY.name,
            country = "CM",
            limit = 3,
            offset = 100,
        )
        val response =
            rest.postForEntity(url(), request, com.wutsi.membership.manager.dto.SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipAccess).searchPlace(
            request = com.wutsi.membership.access.dto.SearchPlaceRequest(
                type = request.type,
                country = request.country,
                limit = request.limit,
                offset = request.offset,
            ),
        )

        val places = response.body!!.places
        assertEquals(2, places.size)
    }

    private fun url() = "http://localhost:$port/v1/places/search"
}
