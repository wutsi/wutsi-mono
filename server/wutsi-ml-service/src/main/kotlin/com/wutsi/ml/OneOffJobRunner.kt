package com.wutsi.ml

import com.wutsi.platform.core.cron.CronJobRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class OneOffJobRunner(
    private val context: ApplicationContext,
    private val jobRegistry: CronJobRegistry,
) : CommandLineRunner {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OneOffJobRunner::class.java)
    }

    override fun run(vararg args: String?) {
        val job = getJob(*args)
        if (job != null) {
            // Run
            LOGGER.info(">>> Running job: $job")
            jobRegistry.get(job)?.run()

            // Shutdown
            LOGGER.info(">>> Bye")
            SpringApplication.exit(context, ExitCodeGenerator { 0 })
        }
    }

    private fun getJob(vararg args: String?): String? {
        val job = args.find { it?.startsWith("-job=") == true }
        return job?.substring(5)
    }
}
