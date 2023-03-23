package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.CreateProductDelegate
import com.wutsi.marketplace.manager.dto.CreateProductRequest
import com.wutsi.marketplace.manager.dto.CreateProductResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateProductController(
    public val `delegate`: CreateProductDelegate,
) {
    @PostMapping("/v1/products")
    public fun invoke(
        @Valid @RequestBody
        request: CreateProductRequest,
    ): CreateProductResponse =
        delegate.invoke(request)
}
