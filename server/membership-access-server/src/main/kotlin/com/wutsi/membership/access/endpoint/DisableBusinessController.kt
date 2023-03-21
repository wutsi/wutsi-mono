package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.delegate.DisableBusinessDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class DisableBusinessController(
    public val `delegate`: DisableBusinessDelegate,
) {
    @DeleteMapping("/v1/accounts/{id}/business")
    public fun invoke(@PathVariable(name = "id") id: Long) {
        delegate.invoke(id)
    }
}
