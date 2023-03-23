package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.GetCategoryDelegate
import com.wutsi.marketplace.manager.dto.GetCategoryResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetCategoryController(
    public val `delegate`: GetCategoryDelegate,
) {
    @GetMapping("/v1/categories/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetCategoryResponse = delegate.invoke(id)
}
