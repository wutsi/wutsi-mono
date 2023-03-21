package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SearchPlaceRequest
import com.wutsi.membership.access.dto.SearchPlaceResponse
import com.wutsi.membership.access.service.PlaceService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SearchPlaceDelegate(
    private val service: PlaceService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    fun invoke(request: SearchPlaceRequest): SearchPlaceResponse {
        logger.add("request_country", request.country)
        logger.add("request_keyboard", request.keyword)
        logger.add("request_type", request.type)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val language = httpRequest.getHeader("Accept-Language")
        val places = service.search(request)
        logger.add("count", places.size)

        return SearchPlaceResponse(
            places = places.map {
                service.toPlaceSummary(it, language)
            },
        )
    }
}
