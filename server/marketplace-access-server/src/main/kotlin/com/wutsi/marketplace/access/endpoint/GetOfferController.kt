package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.GetOfferDelegate
import com.wutsi.marketplace.access.dto.GetOfferResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetOfferController(
    public val `delegate`: GetOfferDelegate,
) {
    @GetMapping("/v1/offers/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetOfferResponse = delegate.invoke(id)
}
