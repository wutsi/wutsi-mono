package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchOfferRequest
import com.wutsi.marketplace.access.dto.SearchOfferResponse
import com.wutsi.marketplace.access.service.OfferService
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SearchOfferDelegate(
    private val service: OfferService,
    private val httpRequest: HttpServletRequest,
) {
    fun invoke(request: SearchOfferRequest): SearchOfferResponse =
        SearchOfferResponse(
            offers = service.search(request, httpRequest.getHeader("Accept-Language")),
        )
}
