package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/update-video-flag")
class UpdateVideoFlagExecutor(
    private val dao: StoryRepository,
    private val service: StoryService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UpdateVideoFlagExecutor::class.java)
    }

    @Async
    @GetMapping
    fun execute() {
        var count = 0
        dao.findByStatus(StoryStatus.PUBLISHED).forEach {
            if (service.updateVideoFlag(it.id!!)) {
                count++
            }
        }
        LOGGER.info("count=$count")
    }
}
