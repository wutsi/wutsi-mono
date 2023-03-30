package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.GetAccountByNameDelegate
import com.wutsi.membership.access.dto.GetAccountResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class GetAccountByNameController(
    public val `delegate`: GetAccountByNameDelegate,
) {
    @GetMapping("/v1/accounts/@{name}")
    public fun invoke(@PathVariable(name = "name") name: String): GetAccountResponse =
        delegate.invoke(name)
}
