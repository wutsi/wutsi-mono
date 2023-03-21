package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateProductAttributeDelegate
import com.wutsi.marketplace.access.dto.UpdateProductAttributeRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateProductAttributeController(
    public val `delegate`: UpdateProductAttributeDelegate,
) {
    @PostMapping("/v1/products/{id}/attributes")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateProductAttributeRequest,
    ) {
        delegate.invoke(id, request)
    }
}
