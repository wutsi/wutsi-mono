package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateProductStatusDelegate
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateProductStatusController(
    public val `delegate`: UpdateProductStatusDelegate,
) {
    @PostMapping("/v1/products/{id}/status")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateProductStatusRequest,
    ) {
        delegate.invoke(id, request)
    }
}
