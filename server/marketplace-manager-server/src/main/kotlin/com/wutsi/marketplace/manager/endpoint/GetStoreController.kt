package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.`delegate`.GetStoreDelegate
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetStoreController(
    public val `delegate`: GetStoreDelegate,
) {
    @GetMapping("/v1/stores/{id}")
    public fun invoke(@PathVariable(name = "id") id: Long): GetStoreResponse = delegate.invoke(id)
}
