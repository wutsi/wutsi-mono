package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.GetFundraisingDelegate
import com.wutsi.marketplace.access.dto.GetFundraisingResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetFundraisingController(
    public val `delegate`: GetFundraisingDelegate,
) {
    @GetMapping("/v1/fundraisings/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetFundraisingResponse = delegate.invoke(id)
}
