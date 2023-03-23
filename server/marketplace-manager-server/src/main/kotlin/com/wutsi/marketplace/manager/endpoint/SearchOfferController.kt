package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.SearchOfferDelegate
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
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
