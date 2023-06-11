package com.wutsi.blog.user.job

import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.ActivateUserCommand
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
class UserActivationJob(
    private val userDao: UserRepository,
    private val userService: UserService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserActivationJob::class.java)
    }

    override fun getJobName() = "user-activation"

    @Scheduled(cron = "\${wutsi.crontab.user-activation}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val threshold = LocalDate.now(ZoneId.of("UTC")).minusMonths(6)
        val users = findUserToActivate(threshold)

        var activated = 0L
        var errors = 0L
        users.forEach {
            val userId = it.id!!
            try {
                LOGGER.info("Activating User#$userId")
                userService.activate(
                    ActivateUserCommand(
                        userId = userId,
                    ),
                )
                activated++
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to deactivate User#$userId", ex)
            }
        }
        logger.add("publication_date_threshold", threshold)
        logger.add("user_count", users.size)
        logger.add("user_activated", activated)
        logger.add("user_errors", errors)
        return activated
    }

    /**
     * Return all non-suspended inactive blog that have published a story in the past 6m
     */
    private fun findUserToActivate(threshold: LocalDate): List<UserEntity> {
        val result = mutableListOf<UserEntity>()
        result.addAll(
            userDao.findByLastPublicationDateTimeGreaterThanAndActiveAndSuspendedAndBlog(
                lastPublicationDateTime = DateUtils.toDate(threshold),
                active = false,
                suspended = false,
                blog = true,
            ),
        )
        return result
    }
}
