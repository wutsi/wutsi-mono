package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchOfferDelegate
import com.wutsi.marketplace.access.dto.SearchOfferRequest
import com.wutsi.marketplace.access.dto.SearchOfferResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchOfferController(
    public val `delegate`: SearchOfferDelegate,
) {
    @PostMapping("/v1/offers/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchOfferRequest,
    ): SearchOfferResponse =
        delegate.invoke(request)
}
