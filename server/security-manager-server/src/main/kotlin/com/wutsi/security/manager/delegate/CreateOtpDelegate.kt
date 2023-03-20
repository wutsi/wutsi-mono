package com.wutsi.security.manager.delegate

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import com.wutsi.security.manager.service.OtpService
import org.springframework.stereotype.Service

@Service
public class CreateOtpDelegate(
    private val service: OtpService,
    private val logger: KVLogger,
) {
    public fun invoke(request: CreateOTPRequest): CreateOTPResponse {
        logger.add("request_address", request.address)
        logger.add("request_type", request.type)

        val otp = service.create(request)
        logger.add("response_token", otp.token)
        logger.add("response_code", otp.code)
        logger.add("response_expires", otp.expires)
        return CreateOTPResponse(
            token = otp.token,
        )
    }
}
