package com.wutsi.blog.account.service

import com.wutsi.blog.account.dao.AccountProviderRepository
import com.wutsi.blog.account.dao.AccountRepository
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.account.dto.UserLoggedInAsEventPayload
import com.wutsi.blog.channel.service.ChannelService
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.USER_LOGGED_IN_AS_EVENT
import com.wutsi.blog.event.EventType.USER_LOGGED_OUT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.service.UserServiceV0
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class AccountService(
    private val clock: Clock,
    private val providerDao: AccountProviderRepository,
    private val accountDao: AccountRepository,
    private val sessionDao: SessionRepository,
    private val userService: UserServiceV0,
    private val logger: KVLogger,
    private val channelService: ChannelService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    fun findSession(token: String): SessionEntity =
        sessionDao
            .findByAccessToken(token)
            .orElseThrow { NotFoundException(Error("session_not_found")) }

    @Transactional
    fun runAs(command: LoginUserAsCommand): SessionEntity {
        logger.add("request_user_name", command.userName)
        logger.add("request_access_token", "****")

        val session = execute(command)
        notify(
            userId = session.account.user.id!!,
            type = USER_LOGGED_IN_AS_EVENT,
            timestamp = command.timestamp,
            payload = UserLoggedInAsEventPayload(session.runAsUser?.id!!)
        )
        return session
    }

    private fun execute(command: LoginUserAsCommand): SessionEntity {
        val session = sessionDao.findByAccessToken(command.accessToken!!)
            .orElseThrow { NotFoundException(Error("account_not_found")) }

        val superUser = session.account.user
        if (!superUser.superUser) {
            throw ConflictException(Error("permission_denied"))
        }

        session.runAsUser = userService.findByName(command.userName!!)
        sessionDao.save(session)
        return session
    }

    @Transactional
    fun logout(command: LogoutUserCommand) {
        val session = execute(command)
        if (session != null) {
            notify(
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

    private fun notify(userId: Long, type: String, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.AUTHENTICATION,
                type = type,
                entityId = userId.toString(),
                userId = userId.toString(),
                timestamp = Date(timestamp),
                payload = payload
            )
        )

        val eventPayload = EventPayload(eventId)
        eventStream.enqueue(type, eventPayload)
        eventStream.publish(type, eventPayload)
    }
}
