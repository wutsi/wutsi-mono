package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetOfferResponse
import com.wutsi.marketplace.access.service.OfferService
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class GetOfferDelegate(
    private val service: OfferService,
    private val httpRequest: HttpServletRequest,
) {
    fun invoke(id: Long): GetOfferResponse =
        GetOfferResponse(
            offer = service.findById(id, httpRequest.getHeader("Accept-Language")),
        )
}
