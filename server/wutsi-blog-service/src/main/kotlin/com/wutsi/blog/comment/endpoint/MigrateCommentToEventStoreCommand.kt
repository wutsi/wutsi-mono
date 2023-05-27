package com.wutsi.blog.like.endpoint

import com.wutsi.blog.event.EventType
import com.wutsi.blog.like.dao.LikeV0Repository
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/likes/commands/migrate-to-event-stream")
class MigrateLikeToEventStoreCommand(
    private val likeDao: LikeV0Repository,
    private val eventStream: EventStream,
) {
    @Async
    @GetMapping
    fun migrate() {
        likeDao.findAll().forEach {
            eventStream.enqueue(
                type = EventType.LIKE_STORY_COMMAND,
                payload = LikeStoryCommand(
                    storyId = it.story.id!!,
                    userId = it.user?.id,
                    deviceId = it.deviceId ?: "-",
                    timestamp = it.likeDateTime.time,
                ),
            )
        }
    }
}
