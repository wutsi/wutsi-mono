package com.wutsi.security.manager.service

import com.auth0.jwt.JWT
import com.wutsi.enums.LoginType
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.TokenBlacklistService
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.LoginRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import com.wutsi.security.manager.entity.LoginEntity
import com.wutsi.security.manager.entity.OtpEntity
import com.wutsi.security.manager.entity.PasswordEntity
import com.wutsi.security.manager.error.ErrorURN
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Optional

@Service
class LoginService(
    private val otpService: OtpService,
    private val passwordService: PasswordService,
    private val keyProvider: RSAKeyProviderImpl,
    private val blacklistService: TokenBlacklistService,
    private val dao: com.wutsi.security.manager.dao.LoginRepository,
    private val logger: KVLogger,
) {
    companion object {
        const val USER_TOKEN_TTL_MILLIS = 84600000L // 1 day
    }

    fun login(request: LoginRequest): String =
        when (request.type) {
            LoginType.MFA.name -> loginMFA(request)
            LoginType.PASSWORD.name -> loginPassword(request)
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.LOGIN_TYPE_NOT_SUPPORTED.urn,
                    parameter = Parameter(
                        name = "type",
                        value = request.type,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }

    private fun loginMFA(request: LoginRequest): String {
        if (request.mfaToken.isNullOrEmpty()) {
            val otp = send(request)
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.AUTHENTICATION_MFA_REQUIRED.urn,
                    data = mapOf(
                        "mfaToken" to otp.token,
                    ),
                ),
            )
        } else {
            return verify(request)
        }
    }

    private fun loginPassword(request: LoginRequest): String {
        val password = passwordService.findByUsername(request.username)
        passwordService.verify(
            accountId = password.accountId,
            request = VerifyPasswordRequest(
                value = request.password ?: "",
            ),
        )
        return createLogin(password).accessToken
    }

    fun logout(accountId: Long) {
        val logins = dao.findByAccountIdAndExpiredIsNull(accountId)
        logger.add("account_id", accountId)
        logger.add("active_logins", logins.size)
        logins.forEach {
            logout(it)
        }
    }

    fun findByAccessToken(accessToken: String): Optional<LoginEntity> =
        dao.findByHash(hash(accessToken))

    private fun logout(login: LoginEntity): LoginEntity {
        // Expire
        login.expired = Date()
        dao.save(login)

        // Blacklist
        val jwt = JWT.decode(login.accessToken)
        val ttl = (jwt.expiresAt.time - System.currentTimeMillis()) / 1000
        logger.add("login_ttl", ttl)
        if (ttl > 0) {
            logger.add("login_id", login.id)
            logger.add("login_blacklisted", true)
            blacklistService.add(login.accessToken, ttl)
        }
        return login
    }

    private fun send(request: LoginRequest): OtpEntity {
        val password = passwordService.findByUsername(request.username)
        val otpRequest = CreateOTPRequest(
            address = password.username,
            type = MessagingType.SMS.name,
        )
        return otpService.create(otpRequest)
    }

    private fun verify(request: LoginRequest): String {
        val otp = otpService.verify(
            token = request.mfaToken!!,
            request = VerifyOTPRequest(
                code = request.verificationCode ?: "",
            ),
        )

        val password = passwordService.findByUsername(otp.address)
        return createLogin(password).accessToken
    }

    private fun createLogin(password: PasswordEntity): LoginEntity {
        val accessToken = JWTBuilder(
            ttl = USER_TOKEN_TTL_MILLIS,
            subjectType = SubjectType.USER,
            name = password.username,
            subject = password.accountId.toString(),
            keyProvider = keyProvider,
        ).build()

        return dao.save(
            LoginEntity(
                accountId = password.accountId,
                hash = hash(accessToken),
                accessToken = accessToken,
                created = Date(),
                expires = Date(System.currentTimeMillis() + USER_TOKEN_TTL_MILLIS),
            ),
        )
    }

    fun hash(accessToken: String): String =
        DigestUtils.md5Hex(accessToken)
}
