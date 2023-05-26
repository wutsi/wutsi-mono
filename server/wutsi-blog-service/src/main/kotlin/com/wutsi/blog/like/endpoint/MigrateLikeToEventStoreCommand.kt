package com.wutsi.blog.like.endpoint

import com.wutsi.blog.like.dao.LikeV0Repository
import com.wutsi.blog.like.dto.LikeEventType
import com.wutsi.blog.like.dto.StoryLikedEvent
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/likes/command/migrate-to-event-store")
class MigrateLikeToEventStoreCommand(
    private val likeDao: LikeV0Repository,
    private val eventStream: EventStream,
) {
    @Async
    @GetMapping
    fun migrate() {
        likeDao.findAll().forEach {
            eventStream.enqueue(
                type = LikeEventType.STORY_LIKED_EVENT,
                payload = StoryLikedEvent(
                    storyId = it.story.id!!,
                    userId = it.user?.id,
                    deviceId = it.deviceId ?: "-",
                    timestamp = it.likeDateTime.time,
                ),
            )
        }
    }
}
