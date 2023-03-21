package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateProductEventDelegate
import com.wutsi.marketplace.access.dto.UpdateProductEventRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateProductEventController(
    public val `delegate`: UpdateProductEventDelegate,
) {
    @PostMapping("/v1/products/{id}/event")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateProductEventRequest,
    ) {
        delegate.invoke(id, request)
    }
}
