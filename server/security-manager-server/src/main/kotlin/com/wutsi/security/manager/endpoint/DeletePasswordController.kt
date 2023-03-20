package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.DeletePasswordDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class DeletePasswordController(
    public val delegate: DeletePasswordDelegate
) {
    @DeleteMapping("/v1/passwords")
    public fun invoke() {
        delegate.invoke()
    }
}
