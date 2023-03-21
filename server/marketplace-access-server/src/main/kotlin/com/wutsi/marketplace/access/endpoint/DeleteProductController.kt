package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.`delegate`.DeleteProductDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class DeleteProductController(
    public val `delegate`: DeleteProductDelegate,
) {
    @DeleteMapping("/v1/products/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long) {
        delegate.invoke(id)
    }
}
