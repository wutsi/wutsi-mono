package com.wutsi.recommendation

import com.wutsi.platform.core.cron.CronJobRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ExecuteJobCommandExecutor(private val registry: CronJobRegistry) {
    @GetMapping("/v1/jobs/commands/execute")
    fun execute(@RequestParam name: String): Map<String, Any> {
        val time = System.currentTimeMillis()
        val job = registry.get(name)
        job?.run()

        return mapOf(
            "run" to (job != null),
            "durationMillis" to System.currentTimeMillis() - time,
        )
    }
}
