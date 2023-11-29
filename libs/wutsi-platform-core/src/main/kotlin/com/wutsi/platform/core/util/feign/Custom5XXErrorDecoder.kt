package com.wutsi.platform.core.util.feign

import feign.FeignException
import feign.Response
import feign.RetryableException
import feign.codec.ErrorDecoder

class Custom5XXErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        val exception = FeignException.errorStatus(methodKey, response)
        if (isRetryable(response)) {
            return RetryableException(
                response.status(),
                response.reason(),
                response.request().httpMethod(),
                exception,
                System.currentTimeMillis(),
                response.request(),
            )
        }

        return exception
    }

    private fun isRetryable(response: Response): Boolean =
        response.status() == 503 || response.status() == 504
}
