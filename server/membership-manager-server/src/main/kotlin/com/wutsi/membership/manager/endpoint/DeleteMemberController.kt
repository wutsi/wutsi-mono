package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.DeleteMemberDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class DeleteMemberController(
    public val `delegate`: DeleteMemberDelegate,
) {
    @DeleteMapping("/v1/members")
    public fun invoke() {
        delegate.invoke()
    }
}
