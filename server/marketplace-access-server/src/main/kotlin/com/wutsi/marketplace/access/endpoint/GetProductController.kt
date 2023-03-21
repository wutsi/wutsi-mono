package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.GetProductDelegate
import com.wutsi.marketplace.access.dto.GetProductResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetProductController(
    public val `delegate`: GetProductDelegate,
) {
    @GetMapping("/v1/products/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetProductResponse = delegate.invoke(id)
}
