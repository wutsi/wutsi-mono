package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.RemoveDiscountProductDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class RemoveDiscountProductController(
    public val `delegate`: RemoveDiscountProductDelegate,
) {
    @DeleteMapping("/v1/discounts/{discount-id}/products/{product-id}")
    public fun invoke(
        @PathVariable(name = "discount-id") discountId: Long,
        @PathVariable(name = "product-id") productId: Long,
    ) {
        delegate.invoke(discountId, productId)
    }
}
