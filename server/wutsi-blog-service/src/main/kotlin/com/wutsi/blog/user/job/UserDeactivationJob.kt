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
        val threshold = LocalDate.now(ZoneId.of("UTC")).minusMonths(6)
        val users = findUserToDeactivate(threshold)

        var deactivated = 0L
        var errors = 0L
        users.forEach {
            val userId = it.id!!
            try {
                LOGGER.info("Deactivating User#$userId")
                userService.deactivate(
                    DeactivateUserCommand(
                        userId = userId,
                    ),
                )
                deactivated++
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to deactivate User#$userId", ex)
            }
        }
        logger.add("user_count", users.size)
        logger.add("user_deactivated", deactivated)
        logger.add("user_errors", errors)
        return deactivated
    }

    /**
     * Return all non-suspended active blog that have not published any story in the past 6m
     */
    private fun findUserToDeactivate(threshold: LocalDate): List<UserEntity> {
        val result = mutableListOf<UserEntity>()
        result.addAll(
            userDao.findByLastPublicationDateTimeLessThanEqualAndActiveAndSuspendedAndBlog(
                lastPublicationDateTime = DateUtils.toDate(threshold),
                active = true,
                suspended = false,
                blog = true,
            ),
        )
        result.addAll(
            userDao.findByLastPublicationDateTimeIsNullAndActiveAndSuspendedAndBlog(
                active = true,
                suspended = false,
                blog = true,
            ),
        )
        return result
    }
}
