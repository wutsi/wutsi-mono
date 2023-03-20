package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.LogoutDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class LogoutController(
    public val delegate: LogoutDelegate
) {
    @DeleteMapping("/v1/auth")
    public fun invoke() {
        delegate.invoke()
    }
}
