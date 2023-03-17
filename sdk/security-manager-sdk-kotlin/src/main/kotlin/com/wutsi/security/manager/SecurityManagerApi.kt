package com.wutsi.security.manager

import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.security.manager.dto.CreatePasswordResponse
import com.wutsi.security.manager.dto.GetKeyResponse
import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.LoginResponse
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import feign.Headers
import feign.Param
import feign.RequestLine
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface SecurityManagerApi {
    @RequestLine("POST /v1/otp")
    @Headers(value = ["Content-Type: application/json"])
    public fun createOtp(request: CreateOTPRequest): CreateOTPResponse

    @RequestLine("POST /v1/otp/{token}/verify")
    @Headers(value = ["Content-Type: application/json"])
    public fun verifyOtp(@Param("token") token: String, request: VerifyOTPRequest): Unit

    @RequestLine("POST /v1/passwords")
    @Headers(value = ["Content-Type: application/json"])
    public fun createPassword(request: CreatePasswordRequest): CreatePasswordResponse

    @RequestLine("DELETE /v1/passwords")
    @Headers(value = ["Content-Type: application/json"])
    public fun deletePassword(): Unit

    @RequestLine("PUT /v1/passwords")
    @Headers(value = ["Content-Type: application/json"])
    public fun updatePassword(request: UpdatePasswordRequest): Unit

    @RequestLine("POST /v1/passwords/verify")
    @Headers(value = ["Content-Type: application/json"])
    public fun verifyPassword(request: VerifyPasswordRequest): Unit

    @RequestLine("GET /v1/keys/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getKey(@Param("id") id: Long): GetKeyResponse

    @RequestLine("POST /v1/auth")
    @Headers(value = ["Content-Type: application/json"])
    public fun login(request: LoginRequest): LoginResponse

    @RequestLine("DELETE /v1/auth")
    @Headers(value = ["Content-Type: application/json"])
    public fun logout(): Unit
}
