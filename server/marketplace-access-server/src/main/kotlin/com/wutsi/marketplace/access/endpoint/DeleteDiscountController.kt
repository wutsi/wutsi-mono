package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.DeleteDiscountDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class DeleteDiscountController(
    public val `delegate`: DeleteDiscountDelegate,
) {
    @DeleteMapping("/v1/discounts/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long) {
        delegate.invoke(id)
    }
}
