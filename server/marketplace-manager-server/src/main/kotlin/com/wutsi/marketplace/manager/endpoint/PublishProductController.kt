package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.PublishProductDelegate
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class PublishProductController(
    public val `delegate`: PublishProductDelegate,
) {
    @PostMapping("/v1/products/{id}/publish")
    public fun invoke(@PathVariable(name = "id") id: Long) {
        delegate.invoke(id)
    }
}
