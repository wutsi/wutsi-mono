package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.VerifyPasswordDelegate
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class VerifyPasswordController(
    public val delegate: VerifyPasswordDelegate,
) {
    @PostMapping("/v1/passwords/verify")
    public fun invoke(
        @Valid @RequestBody
        request: VerifyPasswordRequest,
    ) {
        delegate.invoke(request)
    }
}
