package com.wutsi.security.manager.delegate

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.service.OtpService
import org.springframework.stereotype.Service

@Service
public class VerifyOtpDelegate(
    private val service: OtpService,
    private val logger: KVLogger,
) {
    public fun invoke(token: String, request: VerifyOTPRequest) {
        logger.add("request_code", request.code)

        service.verify(token, request)
    }
}
