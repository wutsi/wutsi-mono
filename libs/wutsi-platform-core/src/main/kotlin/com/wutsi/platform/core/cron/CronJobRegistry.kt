package com.wutsi.platform.core.cron

import org.slf4j.LoggerFactory

open class CronJobRegistry {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CronJobRegistry::class.java)
    }

    private val jobs = mutableMapOf<String, CronJob>()

    open fun register(name: String, cronJob: CronJob) {
        LOGGER.info("Registering $name")

        if (jobs.containsKey(name)) {
            throw IllegalStateException("CronJob[$name] already registered")
        }

        jobs[name] = cronJob
    }

    open fun unregister(name: String) {
        LOGGER.info("Unregistering $name")

        jobs.remove(name)
    }

    open fun get(name: String): CronJob? =
        jobs[name]
}
