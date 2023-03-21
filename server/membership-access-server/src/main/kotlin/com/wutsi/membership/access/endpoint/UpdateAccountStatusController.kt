package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.UpdateAccountStatusDelegate
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateAccountStatusController(
    public val `delegate`: UpdateAccountStatusDelegate,
) {
    @PostMapping("/v1/accounts/{id}/status")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateAccountStatusRequest,
    ) {
        delegate.invoke(id, request)
    }
}
