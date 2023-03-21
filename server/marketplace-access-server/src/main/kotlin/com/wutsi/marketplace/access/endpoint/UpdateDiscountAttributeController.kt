package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateDiscountAttributeDelegate
import com.wutsi.marketplace.access.dto.UpdateDiscountAttributeRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateDiscountAttributeController(
    public val `delegate`: UpdateDiscountAttributeDelegate,
) {
    @PostMapping("/v1/discounts/{id}/attributes")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateDiscountAttributeRequest,
    ) {
        delegate.invoke(id, request)
    }
}
