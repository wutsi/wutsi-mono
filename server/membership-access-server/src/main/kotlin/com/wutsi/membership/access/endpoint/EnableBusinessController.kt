package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.EnableBusinessDelegate
import com.wutsi.membership.access.dto.EnableBusinessRequest
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class EnableBusinessController(
    public val `delegate`: EnableBusinessDelegate,
) {
    @PostMapping("/v1/accounts/{id}/business")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: EnableBusinessRequest,
    ) {
        delegate.invoke(id, request)
    }
}
