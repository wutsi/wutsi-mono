package com.wutsi.blog.account.job

import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.account.service.LoginService
import com.wutsi.blog.event.EventType.LOGOUT_USER_COMMAND
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SessionExpirerJob(
    private val loginService: LoginService,
    private val logger: KVLogger,
    private val eventStream: EventStream,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "session-expirer"

    @Scheduled(cron = "\${wutsi.crontab.session-expirer}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val sessions = loginService.findSessionsToExpire()

        var expired = 0L
        var errors = 0L
        sessions.forEach {
            try {
                eventStream.publish(
                    LOGOUT_USER_COMMAND,
                    LogoutUserCommand(accessToken = it.accessToken),
                )
                expired++
            } catch (ex: Exception) {
                errors++
            }
        }

        logger.add("session_count", sessions.size)
        logger.add("expired_count", expired)
        logger.add("error_count", errors)

        return expired
    }
}
