package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.CreateMemberDelegate
import com.wutsi.membership.manager.dto.CreateMemberRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateMemberController(
    public val `delegate`: CreateMemberDelegate,
) {
    @PostMapping("/v1/members")
    public fun invoke(
        @Valid @RequestBody
        request: CreateMemberRequest,
    ) {
        delegate.invoke(request)
    }
}
