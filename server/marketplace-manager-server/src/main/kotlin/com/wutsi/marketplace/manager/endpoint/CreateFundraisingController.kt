package com.wutsi.marketplace.manager.endpoint

import com.wutsi.marketplace.manager.delegate.CreateFundraisingDelegate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class CreateFundraisingController(
    public val `delegate`: CreateFundraisingDelegate,
) {
    @PostMapping("/v1/fundraisings")
    public fun invoke() {
        delegate.invoke()
    }
}
