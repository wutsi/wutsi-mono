package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchStoreDelegate
import com.wutsi.marketplace.access.dto.SearchStoreRequest
import com.wutsi.marketplace.access.dto.SearchStoreResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchStoreController(
    public val `delegate`: SearchStoreDelegate,
) {
    @PostMapping("/v1/stores/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchStoreRequest,
    ): SearchStoreResponse =
        delegate.invoke(request)
}
