package com.wutsi.blog.mail.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType.SEND_STORY_DAILY_EMAIL_COMMAND
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus.PUBLISHED
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val eventStream: EventStream,
) {
    fun sendDailyEmail(): Long {
        val yesterday = DateUtils.toDate(LocalDate.now().minusDays(1))
        logger.add("date", yesterday)

        val stories = storyService.searchStories(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                status = PUBLISHED,
                publishedStartDate = yesterday,
                limit = 100,
            ),
        )
        logger.add("story_count", stories.size)

        stories.forEach {
            eventStream.enqueue(
                type = SEND_STORY_DAILY_EMAIL_COMMAND,
                payload = SendStoryDailyEmailCommand(
                    storyId = it.id!!,
                ),
            )
        }
        return stories.size.toLong()
    }
}
