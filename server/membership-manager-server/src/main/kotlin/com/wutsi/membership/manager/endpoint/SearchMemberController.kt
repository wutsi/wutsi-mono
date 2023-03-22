package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.SearchMemberDelegate
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchMemberController(
    public val `delegate`: SearchMemberDelegate,
) {
    @PostMapping("/v1/members/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchMemberRequest,
    ): SearchMemberResponse =
        delegate.invoke(request)
}
