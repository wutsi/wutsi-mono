package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.delegate.VerifyOtpDelegate
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
public class VerifyOtpController(
    public val delegate: VerifyOtpDelegate,
) {
    @PostMapping("/v1/otp/{token}/verify")
    public fun invoke(
        @PathVariable(name = "token") token: String,
        @Valid @RequestBody
        request: VerifyOTPRequest,
    ) {
        delegate.invoke(token, request)
    }
}
