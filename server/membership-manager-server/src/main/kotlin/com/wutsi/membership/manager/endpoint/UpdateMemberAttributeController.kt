package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.UpdateMemberAttributeDelegate
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class UpdateMemberAttributeController(
    public val `delegate`: UpdateMemberAttributeDelegate,
) {
    @PostMapping("/v1/members/attributes")
    public fun invoke(
        @Valid @RequestBody
        request: UpdateMemberAttributeRequest,
    ) {
        delegate.invoke(request)
    }
}
