package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.CreateStoreDelegate
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.CreateStoreResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateStoreController(
    public val `delegate`: CreateStoreDelegate,
) {
    @PostMapping("/v1/stores")
    public fun invoke(
        @Valid @RequestBody
        request: CreateStoreRequest,
    ): CreateStoreResponse =
        delegate.invoke(request)
}
