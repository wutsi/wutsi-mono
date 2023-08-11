package com.wutsi.ml

import com.wutsi.platform.core.cron.CronJobRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Service

@Service
class OneOffCommandLineRunner(
    private val context: ApplicationContext,
    private val jobRegistry: CronJobRegistry,
) : CommandLineRunner {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OneOffCommandLineRunner::class.java)
    }

    override fun run(vararg args: String?) {
        val job = getJob(*args)
        if (job != null) {
            // Run
            LOGGER.info(">>> Running job: $job")
            jobRegistry.get(job)?.run()

            // Shutdown
            LOGGER.info(">>> Bye")
            (context as ConfigurableApplicationContext).close()
        }
    }

    private fun getJob(vararg args: String?): String? {
        val job = args.find { it?.startsWith("-job=") == true }
        return job?.substring(5)
    }
}
