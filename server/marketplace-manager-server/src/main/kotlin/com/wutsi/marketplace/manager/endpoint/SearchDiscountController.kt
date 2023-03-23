package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.SearchDiscountDelegate
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.marketplace.manager.dto.SearchDiscountResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchDiscountController(
    public val `delegate`: SearchDiscountDelegate,
) {
    @PostMapping("/v1/discounts/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchDiscountRequest,
    ): SearchDiscountResponse =
        delegate.invoke(request)
}
