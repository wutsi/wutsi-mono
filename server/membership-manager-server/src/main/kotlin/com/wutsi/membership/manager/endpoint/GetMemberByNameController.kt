package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.GetMemberByNameDelegate
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class GetMemberByNameController(
    public val `delegate`: GetMemberByNameDelegate,
) {
    @GetMapping("/v1/members/@{name}")
    public fun invoke(@PathVariable(name = "name") name: String): GetMemberResponse =
        delegate.invoke(name)
}
