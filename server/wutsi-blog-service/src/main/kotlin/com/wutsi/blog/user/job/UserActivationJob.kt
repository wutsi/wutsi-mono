package com.wutsi.blog.user.job

import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.DeactivateUserCommand
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class UserDeactivationJob(
    private val userDao: UserRepository,
    private val userService: UserService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserDeactivationJob::class.java)
    }

    override fun getJobName() = "user-deactivation"

    @Scheduled(cron = "\${wutsi.crontab.user-deactivation}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val users = findUserToDeactivate()

        var deactivated = 0L
        var errors = 0L
        users.forEach {
            try {
                LOGGER.info("Deactivating User#${it.id}")
                userService.deactivate(
                    DeactivateUserCommand(
                        userId = it.id!!
                    ),
                )
                deactivated++
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to deactivate User#${it.id}", ex)
            }
        }
        logger.add("user_count", users.size)
        logger.add("user_deactivated", deactivated)
        logger.add("user_errors", errors)
        return deactivated
    }

    private fun findUserToDeactivate(): List<UserEntity> {
        val threshold = LocalDate.now(ZoneId.of("UTC")).minusMonths(6)
        val result = mutableListOf<UserEntity>()
        result.addAll(
            userDao.findByLastPublicationDateTimeLessThanAndActiveAndSuspendedAndBlog(
                lastPublicationDateTime = DateUtils.toDate(threshold),
                active = true,
                suspended = false,
                blog = true,
            )
        )
        result.addAll(
            userDao.findByLastPublicationDateTimeIsNullAndActiveAndSuspendedAndBlog(
                active = true,
                suspended = false,
                blog = true,
            )
        )
        return result
    }
}
