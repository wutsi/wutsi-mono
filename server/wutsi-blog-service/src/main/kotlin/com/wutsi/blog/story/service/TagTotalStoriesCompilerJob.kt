package com.wutsi.blog.story.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TagTotalStoriesCompilerJob(
    private val tags: TagService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TagTotalStoriesCompilerJob::class.java)
    }

    @Scheduled(cron = "\${wutsi.crontab.compile-tags}")
    fun compile() {
        LOGGER.info("Running")

        try {
            val updated = tags.updateTotalStories()
            LOGGER.error("Done. $updated tag(s) updated")
        } catch (ex: Exception) {
            LOGGER.error("Done. Unexpected error", ex)
        }
    }
}
