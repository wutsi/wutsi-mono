package com.wutsi.blog.account.service

import com.wutsi.blog.account.dao.AccountProviderRepository
import com.wutsi.blog.account.dao.AccountRepository
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.domain.AccountEntity
import com.wutsi.blog.account.domain.AccountProviderEntity
import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.account.dto.UserLoggedInAsEventPayload
import com.wutsi.blog.channel.service.ChannelService
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_AS_EVENT
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_EVENT
import com.wutsi.blog.event.EventType.USER_LOGGED_OUT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class LoginService(
    private val providerDao: AccountProviderRepository,
    private val accountDao: AccountRepository,
    private val sessionDao: SessionRepository,
    private val userService: UserService,
    private val logger: KVLogger,
    private val channelService: ChannelService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginService::class.java)
    }

    fun findSession(token: String): SessionEntity {
        val session = sessionDao
            .findByAccessToken(token)
            .orElseThrow { NotFoundException(Error(ErrorCode.SESSION_NOT_FOUND)) }

        if (session.logoutDateTime != null) {
            throw NotFoundException(Error(ErrorCode.SESSION_EXPIRED))
        }
        return session
    }

    fun findSessionsToExpire(): List<SessionEntity> {
        val date = DateUtils.addDays(Date(), -1)
        return sessionDao.findByLoginDateTimeLessThanAndLogoutDateTimeNull(date)
    }

    private fun findProvider(name: String) = providerDao
        .findByNameIgnoreCase(name)
        .orElseThrow { ConflictException(Error("invalid_provider")) }

    @Transactional
    fun loginAs(command: LoginUserAsCommand): SessionEntity {
        logger.add("request_user_name", command.userName)
        logger.add("request_access_token", "****")
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "LoginUserAsCommand")

        val session = execute(command)
        notify(
            accessToken = session.accessToken,
            userId = session.account.user.id!!,
            type = USER_LOGGED_IN_AS_EVENT,
            timestamp = command.timestamp,
            payload = UserLoggedInAsEventPayload(session.runAsUser?.id!!),
        )
        return session
    }

    private fun execute(command: LoginUserAsCommand): SessionEntity {
        val session = sessionDao.findByAccessToken(command.accessToken)
            .orElseThrow { NotFoundException(Error("account_not_found")) }

        val superUser = session.account.user
        if (!superUser.superUser) {
            throw ConflictException(Error(ErrorCode.PERMISSION_DENIED))
        }

        session.runAsUser = userService.findByName(command.userName)
        sessionDao.save(session)
        return session
    }

    @Transactional
    fun login(command: LoginUserCommand): SessionEntity {
        logger.add("request_full_name", command.fullName)
        logger.add("request_email", command.email)
        logger.add("request_picture_url", command.pictureUrl)
        logger.add("request_language", command.language)
        logger.add("request_provider", command.provider)
        logger.add("request_provider_user_id", command.providerUserId)
        logger.add("request_access_token", "***")
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "LoginUserCommand")

        val session = execute(command)
        notify(
            accessToken = session.accessToken,
            userId = session.account.user.id!!,
            type = USER_LOGGED_IN_EVENT,
            timestamp = command.timestamp,
        )
        return session
    }

    private fun execute(command: LoginUserCommand): SessionEntity {
        val opt = sessionDao.findByAccessToken(command.accessToken)
        return if (opt.isPresent) {
            opt.get()
        } else {
            val account = findOrCreateAccount(command)
            if (account.user.suspended) {
                throw NotFoundException(Error(ErrorCode.USER_SUSPENDED))
            }

            sessionDao.save(
                SessionEntity(
                    accessToken = command.accessToken,
                    refreshToken = command.refreshToken,
                    account = account,
                    loginDateTime = Date(command.timestamp),
                ),
            )
        }
    }

    @Transactional
    fun onLogin(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val session = findSession(event.entityId)
        userService.onLoggedIn(session)
    }

    @Transactional
    fun logout(command: LogoutUserCommand) {
        logger.add("request_access_token", command.accessToken)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "LogoutCommand")
        val session = execute(command)
        if (session != null) {
            notify(
                accessToken = session.accessToken,
                userId = session.account.user.id!!,
                type = USER_LOGGED_OUT_EVENT,
                timestamp = command.timestamp,
            )
        }
    }

    private fun execute(command: LogoutUserCommand): SessionEntity? {
        val session = sessionDao.findByAccessToken(command.accessToken).getOrNull() ?: return null
        if (session.logoutDateTime == null) {
            session.logoutDateTime = Date()
            return sessionDao.save(session)
        }
        return null
    }

    private fun findOrCreateAccount(command: LoginUserCommand): AccountEntity {
        val provider = findProvider(command.provider)
        return accountDao.findByProviderUserIdAndProvider(command.providerUserId, provider)
            .orElseGet { findOrCreateAccount(provider, command) }
    }

    private fun findOrCreateAccount(provider: AccountProviderEntity, command: LoginUserCommand): AccountEntity {
        val user = findOrCreateUser(command)
        return accountDao
            .findByUserAndProvider(user, provider)
            .orElseGet { createAccount(user, provider, command) }
    }

    private fun createAccount(
        user: UserEntity,
        provider: AccountProviderEntity,
        command: LoginUserCommand,
    ): AccountEntity {
        val account = AccountEntity(
            user = user,
            provider = provider,
            providerUserId = command.providerUserId,
        )
        return accountDao.save(account)
    }

    private fun findOrCreateUser(command: LoginUserCommand): UserEntity {
        val user: UserEntity? = findUserFromChannel(command)
        if (user != null) {
            return user
        }

        return if (command.email == null) {
            userService.createUser(
                fullName = command.fullName,
                email = null,
                providerUserId = command.providerUserId,
                pictureUrl = command.pictureUrl,
                language = command.language,
            )
        } else {
            try {
                userService.findByEmail(command.email!!)
            } catch (ex: NotFoundException) {
                userService.createUser(
                    fullName = command.fullName,
                    email = command.email,
                    providerUserId = command.providerUserId,
                    pictureUrl = command.pictureUrl,
                    language = command.language,
                )
            }
        }
    }

    private fun findUserFromChannel(command: LoginUserCommand): UserEntity? =
        try {
            val type = ChannelType.valueOf(command.provider)
            val userId = channelService.findChannel(command.providerUserId, type).userId

            userService.findById(userId)
        } catch (ex: Exception) {
            null
        }

    fun notify(accessToken: String, userId: Long, type: String, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.AUTHENTICATION,
                type = type,
                entityId = accessToken,
                userId = userId.toString(),
                timestamp = Date(timestamp),
                payload = payload,
            ),
        )

        val eventPayload = EventPayload(eventId)

        // We do not want to login to fail because of notification error!!!
        try {
            eventStream.enqueue(type, eventPayload)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to enqueue to type=$type payload=$payload", ex)
        }
        try {
            eventStream.publish(type, eventPayload)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to publish to type=$type payload=$payload", ex)
        }
    }
}
