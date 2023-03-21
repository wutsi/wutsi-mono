package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateStoreStatusDelegate
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateStoreStatusController(
    public val `delegate`: UpdateStoreStatusDelegate,
) {
    @PostMapping("/v1/stores/{id}/status")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateStoreStatusRequest,
    ) {
        delegate.invoke(id, request)
    }
}
