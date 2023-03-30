package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateFundraisingStatusDelegate
import com.wutsi.marketplace.access.dto.UpdateFundraisingStatusRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateFundraisingStatusController(
    public val `delegate`: UpdateFundraisingStatusDelegate,
) {
    @PostMapping("/v1/fundraisings/{id}/status")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateFundraisingStatusRequest,
    ) {
        delegate.invoke(id, request)
    }
}
