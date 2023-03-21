package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.GetDiscountDelegate
import com.wutsi.marketplace.access.dto.GetDiscountResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetDiscountController(
    public val `delegate`: GetDiscountDelegate,
) {
    @GetMapping("/v1/discounts/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetDiscountResponse = delegate.invoke(id)
}
