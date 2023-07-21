package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryBagOfWordJob(
    private val service: StoryService,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "story-bag-of-word"

    @Scheduled(cron = "\${wutsi.crontab.story-bag-of-word}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return service.generateCorpusBagOfWords(false)
    }
}
