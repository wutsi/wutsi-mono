package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.CreateDiscountDelegate
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.marketplace.manager.dto.CreateDiscountResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateDiscountController(
    public val `delegate`: CreateDiscountDelegate,
) {
    @PostMapping("/v1/discounts")
    public fun invoke(
        @Valid @RequestBody
        request: CreateDiscountRequest,
    ): CreateDiscountResponse =
        delegate.invoke(request)
}
