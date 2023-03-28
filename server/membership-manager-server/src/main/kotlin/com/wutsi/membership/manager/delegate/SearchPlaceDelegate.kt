package com.wutsi.membership.manager.delegate

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.dto.PlaceSummary
import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import org.springframework.stereotype.Service

@Service
public class SearchPlaceDelegate(
    private val membershipAccessApi: MembershipAccessApi,
) {
    public fun invoke(request: SearchPlaceRequest): SearchPlaceResponse {
        val places = membershipAccessApi.searchPlace(
            request = com.wutsi.membership.access.dto.SearchPlaceRequest(
                keyword = request.keyword,
                type = request.type,
                country = request.country,
                limit = request.limit,
                offset = request.offset,
            ),
        ).places

        return SearchPlaceResponse(
            places = places.map {
                PlaceSummary(
                    id = it.id,
                    type = it.type,
                    name = it.name,
                    longName = it.longName,
                )
            },
        )
    }
}
