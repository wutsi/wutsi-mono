package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.SearchCategoryDelegate
import com.wutsi.marketplace.access.dto.SearchCategoryRequest
import com.wutsi.marketplace.access.dto.SearchCategoryResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchCategoryController(
    public val `delegate`: SearchCategoryDelegate,
) {
    @PostMapping("/v1/categories/search")
    public fun invoke(
        @Valid @RequestBody
        request: SearchCategoryRequest,
    ): SearchCategoryResponse =
        delegate.invoke(request)
}
