package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreateFundraisingDelegate
import com.wutsi.marketplace.access.dto.CreateFundraisingRequest
import com.wutsi.marketplace.access.dto.CreateFundraisingResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateFundraisingController(
    public val `delegate`: CreateFundraisingDelegate,
) {
    @PostMapping("/v1/fundraisings")
    public fun invoke(
        @Valid @RequestBody
        request: CreateFundraisingRequest,
    ):
        CreateFundraisingResponse = delegate.invoke(request)
}
