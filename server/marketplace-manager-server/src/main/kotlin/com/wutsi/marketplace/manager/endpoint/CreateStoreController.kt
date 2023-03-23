package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.delegate.CreateStoreDelegate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class CreateStoreController(
    public val `delegate`: CreateStoreDelegate,
) {
    @PostMapping("/v1/stores")
    public fun invoke() = delegate.invoke()
}
