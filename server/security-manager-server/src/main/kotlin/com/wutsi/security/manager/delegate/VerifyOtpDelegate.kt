package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.springframework.stereotype.Service

@Service
public class VerifyOtpDelegate() {
    public fun invoke(token: String, request: VerifyOTPRequest) {
        TODO()
    }
}
