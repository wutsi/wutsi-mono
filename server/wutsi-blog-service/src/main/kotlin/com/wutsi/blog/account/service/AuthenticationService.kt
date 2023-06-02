package com.wutsi.blog.account.service

import com.wutsi.blog.account.dao.AccountProviderRepository
import com.wutsi.blog.account.dao.AccountRepository
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.domain.Account
import com.wutsi.blog.account.domain.AccountProvider
import com.wutsi.blog.account.domain.Session
import com.wutsi.blog.channel.service.ChannelService
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.RunAsRequest
import com.wutsi.blog.client.user.RunAsResponse
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.Date

@Service
class AuthenticationService(
    private val clock: Clock,
    private val providerDao: AccountProviderRepository,
    private val accountDao: AccountRepository,
    private val sessionDao: SessionRepository,
    private val userService: UserServiceV0,
    private val logger: KVLogger,
    private val channelService: ChannelService,
) {
    fun findByAccessToken(token: String): Session {
        val session = sessionDao
            .findByAccessToken(token)
            .orElseThrow { NotFoundException(Error("session_not_found")) }

        if (session.logoutDateTime != null) {
            throw NotFoundException(Error("session_expired"))
        }
        return session
    }

    @Transactional
    fun login(request: AuthenticateRequest): AuthenticateResponse {
        log(request)
        val session: Session = loginUser(request)
        log(session)

        return AuthenticateResponse(
            accessToken = session.accessToken,
            sessionId = session.id!!,
            userId = session.account.user.id!!,
            loginCount = session.account.user.loginCount,
        )
    }

    fun loginUser(request: AuthenticateRequest): Session {
        val opt = sessionDao.findByAccessToken(request.accessToken!!)
        val session: Session
        if (opt.isPresent) {
            session = opt.get()
        } else {
            val account = findOrCreateAccount(request)
            session = startSession(account, request)
        }
        return session
    }

    @Transactional
    fun runAs(request: RunAsRequest): RunAsResponse {
        log(request)

        val session = sessionDao.findByAccessToken(request.accessToken!!)
            .orElseThrow { NotFoundException(Error("account_not_found")) }

        val superUser = session.account.user
        if (!superUser.superUser) {
            throw ConflictException(Error("permission_denied"))
        }

        session.runAsUser = userService.findByName(request.userName!!)
        sessionDao.save(session)

        return RunAsResponse(
            accessToken = request.accessToken!!,
        )
    }

    @Transactional
    fun logout(token: String) {
        val session = findByAccessToken(token)
        stopSession(session)
    }

    private fun log(request: RunAsRequest) {
        logger.add("Name", request.userName)
        logger.add("AccessToken", request.accessToken)
    }

    private fun log(request: AuthenticateRequest) {
        logger.add("FullName", request.fullName)
        logger.add("Email", request.email)
        logger.add("PictureUrl", request.pictureUrl)
        logger.add("AccessToken", request.accessToken)
        logger.add("Provider", request.provider)
        logger.add("ProviderUserId", request.providerUserId)
    }

    private fun log(session: Session) {
        logger.add("AccountId", session.account.id)
        logger.add("UserId", session.account.user.id)
    }

    private fun findOrCreateAccount(request: AuthenticateRequest): Account {
        val provider = findProvider(request)
        return accountDao
            .findByProviderUserIdAndProvider(request.providerUserId!!, provider)
            .orElseGet { findOrCreateAccount(provider, request) }
    }

    private fun findOrCreateAccount(provider: AccountProvider, request: AuthenticateRequest): Account {
        val user = findOrCreateUser(request)
        return accountDao
            .findByUserAndProvider(user, provider)
            .orElseGet { createAccount(user, provider, request) }
    }

    private fun createAccount(user: UserEntity, provider: AccountProvider, request: AuthenticateRequest): Account {
        val account = Account(
            user = user,
            provider = provider,
            providerUserId = request.providerUserId!!,
        )
        return accountDao.save(account)
    }

    private fun findOrCreateUser(request: AuthenticateRequest): UserEntity {
        var user: UserEntity? = findUserFromChannel(request)
        if (user != null) {
            if (user.suspended) {
                throw NotFoundException(Error("user_suspended"))
            }

            return user
        }

        if (request.email == null) {
            user = userService.createUser(request)
        } else {
            try {
                user = userService.findByEmail(request.email!!)
            } catch (ex: NotFoundException) {
                user = userService.createUser(request)
            }
        }

        return user!!
    }

    private fun findUserFromChannel(request: AuthenticateRequest): UserEntity? {
        try {
            val type = ChannelType.valueOf(request.provider!!)
            val userId = channelService.findChannel(request.providerUserId!!, type).userId
            return userService.findById(userId)
        } catch (ex: Exception) {
            return null
        }
    }

    private fun startSession(account: Account, request: AuthenticateRequest): Session {
        val now = Date(clock.millis())
        sessionStarted(now, account, request)
        return sessionDao.save(
            Session(
                accessToken = request.accessToken!!,
                refreshToken = request.refreshToken,
                account = account,
                loginDateTime = now,
            ),
        )
    }

    private fun sessionStarted(now: Date, account: Account, request: AuthenticateRequest) {
        account.lastLoginDateTime = now
        account.loginCount = account.loginCount + 1
        accountDao.save(account)

        userService.sessionStarted(now, account.user, request)
    }

    private fun stopSession(session: Session) {
        session.logoutDateTime = Date(clock.millis())
        sessionDao.save(session)
    }

    private fun findProvider(request: AuthenticateRequest) = providerDao
        .findByNameIgnoreCase(request.provider!!)
        .orElseThrow { ConflictException(Error("invalid_provider")) }
}
