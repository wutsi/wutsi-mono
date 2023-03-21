package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreateDiscountDelegate
import com.wutsi.marketplace.access.dto.CreateDiscountRequest
import com.wutsi.marketplace.access.dto.CreateDiscountResponse
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
