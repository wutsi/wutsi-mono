package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.SearchProductDelegate
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.marketplace.manager.dto.SearchProductResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchProductController(
    public val `delegate`: SearchProductDelegate,
) {
    @PostMapping("/v1/products/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchProductRequest,
    ): SearchProductResponse =
        delegate.invoke(request)
}
