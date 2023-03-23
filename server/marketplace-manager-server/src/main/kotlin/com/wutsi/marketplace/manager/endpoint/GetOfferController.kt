package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.GetOfferDelegate
import com.wutsi.marketplace.manager.dto.GetOfferResponse
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
