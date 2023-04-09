package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.UpdateFundraisingAttributeDelegate
import com.wutsi.marketplace.access.dto.UpdateFundraisingAttributeRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateFundraisingAttributeController(
    public val `delegate`: UpdateFundraisingAttributeDelegate,
) {
    @PostMapping("/v1/fundraisings/{id}/attributes")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateFundraisingAttributeRequest,
    ) {
        delegate.invoke(id, request)
    }
}
