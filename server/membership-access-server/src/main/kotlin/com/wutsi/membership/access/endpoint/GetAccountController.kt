package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.GetAccountDelegate
import com.wutsi.membership.access.dto.GetAccountResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetAccountController(
    public val `delegate`: GetAccountDelegate,
) {
    @GetMapping("/v1/accounts/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetAccountResponse = delegate.invoke(id)
}
