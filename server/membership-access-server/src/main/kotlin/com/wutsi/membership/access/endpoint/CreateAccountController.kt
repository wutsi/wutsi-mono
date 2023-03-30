package com.wutsi.membership.access.endpoint

import com.wutsi.membership.access.`delegate`.CreateAccountDelegate
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateAccountController(
    public val `delegate`: CreateAccountDelegate,
) {
    @PostMapping("/v1/accounts")
    public fun invoke(
        @Valid @RequestBody
        request: CreateAccountRequest,
    ): CreateAccountResponse =
        delegate.invoke(request)
}
