package com.wutsi.platform.core.error.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_COOKIE
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_HEADER
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_QUERY
import com.wutsi.platform.core.error.exception.WutsiException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.servlet.HttpTracingContext
import feign.FeignException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class RestControllerErrorHandler(
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val ERROR_REQUEST_NOT_READABLE = "urn:error:wutsi:request-not-readable"
        const val ERROR_MISSING_PARAMETER = "urn:error:wutsi:missing-parameter"
        const val ERROR_INVALID_PARAMETER = "urn:error:wutsi:invalid-parameter"
        const val ERROR_INTERNAL = "urn:error:wutsi:unexpected-error"
        const val ERROR_METHOD_NOT_SUPPORTED = "urn:error:wutsi:method-not-supported"
        const val ERROR_ACCESS_DENIED = "urn:error:wutsi:access-denied"
        const val ERROR_AUTHENTICATION_FAILED = "urn:error:wutsi:authetication-failed"
        const val ERROR_DOWNSTREAM_ERROR = "urn:error:wutsi:downstream-error"
    }

    private val tracingContext = HttpTracingContext()

    @ExceptionHandler(FeignException::class)
    fun onFeignException(request: HttpServletRequest, ex: FeignException): ResponseEntity<ErrorResponse> {
        try {
            val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            return handleException(
                request = request,
                status = HttpStatus.valueOf(ex.status()),
                e = ex,
                code = ERROR_DOWNSTREAM_ERROR,
                downstreamCode = response.error.code,
            )
        } catch (ex: FeignException) {
            return handleException(
                request = request,
                status = HttpStatus.valueOf(ex.status()),
                e = ex,
                code = ERROR_DOWNSTREAM_ERROR,
            )
        }
    }

    @ExceptionHandler(Throwable::class)
    fun onException(request: HttpServletRequest, e: Throwable): ResponseEntity<ErrorResponse> {
        val error = if (e is WutsiException) e.error else null
        return handleException(
            request,
            code = error?.code ?: ERROR_INTERNAL,
            status = status(e),
            e = e,
            parameter = error?.parameter,
            message = error?.message ?: e.message,
            data = error?.data,
            downstreamCode = error?.downstreamCode,
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun onAccessDeniedException(request: HttpServletRequest, e: AccessDeniedException): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ERROR_ACCESS_DENIED,
            status = FORBIDDEN,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(AuthenticationException::class)
    fun onAuthenticationException(
        request: HttpServletRequest,
        e: AuthenticationException,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ERROR_AUTHENTICATION_FAILED,
            status = UNAUTHORIZED,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun onHttpRequestMethodNotSupportedException(
        request: HttpServletRequest,
        e: HttpRequestMethodNotSupportedException,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code = ERROR_METHOD_NOT_SUPPORTED,
            status = HttpStatus.NOT_FOUND,
            e = e,
            message = e.message,
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun onHttpMessageNotReadableException(
        request: HttpServletRequest,
        e: HttpMessageNotReadableException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(request, ERROR_REQUEST_NOT_READABLE, e)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun onMissingServletRequestParameterException(
        request: HttpServletRequest,
        e: MissingServletRequestParameterException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            code = ERROR_MISSING_PARAMETER,
            e = e,
            parameter = Parameter(
                name = e.parameterName,
                type = PARAMETER_TYPE_QUERY,
            ),
        )

    @ExceptionHandler(MissingPathVariableException::class)
    fun onMissingPathVariableException(
        request: HttpServletRequest,
        e: MissingPathVariableException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ERROR_MISSING_PARAMETER,
            e,
            Parameter(
                name = e.variableName,
                type = PARAMETER_TYPE_PATH,
            ),
        )

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun onMissingRequestHeaderException(
        request: HttpServletRequest,
        e: MissingRequestHeaderException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ERROR_MISSING_PARAMETER,
            e,
            Parameter(
                name = e.headerName,
                type = PARAMETER_TYPE_HEADER,
            ),
        )

    @ExceptionHandler(MissingRequestCookieException::class)
    fun onMissingRequestCookieException(
        request: HttpServletRequest,
        e: MissingRequestCookieException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ERROR_MISSING_PARAMETER,
            e,
            Parameter(
                name = e.cookieName,
                type = PARAMETER_TYPE_COOKIE,
            ),
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onMethodArgumentNotValidException(
        request: HttpServletRequest,
        e: MethodArgumentNotValidException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ERROR_INVALID_PARAMETER,
            e,
            Parameter(
                name = e.parameter.parameterName ?: "",
            ),
        )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun onMethodArgumentTypeMismatchException(
        request: HttpServletRequest,
        e: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ErrorResponse> =
        handleBadRequest(
            request,
            ERROR_INVALID_PARAMETER,
            e,
            Parameter(
                name = e.parameter.parameterName ?: "",
                value = e.value,
            ),
        )

    private fun handleBadRequest(
        request: HttpServletRequest,
        code: String,
        e: Throwable,
        parameter: Parameter? = null,
    ): ResponseEntity<ErrorResponse> =
        handleException(
            request,
            code,
            BAD_REQUEST,
            e,
            parameter,
            e.message,
        )

    private fun handleException(
        request: HttpServletRequest,
        code: String,
        status: HttpStatus,
        e: Throwable,
        parameter: Parameter? = null,
        message: String? = null,
        data: Map<String, Any>? = null,
        downstreamCode: String? = null,
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            error = Error(
                code = code,
                traceId = tracingContext.traceId(request),
                parameter = parameter,
                message = message,
                data = data,
                downstreamCode = downstreamCode,
            ),
        )

        log(response.error, e)
        return ResponseEntity
            .status(status)
            .body(response)
    }

    private fun log(error: Error, e: Throwable) {
        logger.add("error_code", error.code)
        logger.add("error_message", error.message)
        logger.add("error_downstream_code", error.downstreamCode)
        logger.add("error_downstream_message", error.downstreamMessage)
        logger.add("error_parameter_name", error.parameter?.name)
        logger.add("error_parameter_value", error.parameter?.value)
        logger.add("error_parameter_type", error.parameter?.type)
        error.data?.forEach {
            logger.add("error_data_${it.key}", it.value)
        }

        logger.setException(e)
    }

    private fun status(e: Throwable): HttpStatus {
        val status = e::class.annotations.find { it is ResponseStatus } as ResponseStatus?
        return status?.value ?: INTERNAL_SERVER_ERROR
    }
}
