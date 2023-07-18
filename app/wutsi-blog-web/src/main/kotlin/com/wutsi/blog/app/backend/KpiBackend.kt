package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class LikeBackend(
    private val eventStream: EventStream,
) {
    fun like(cmd: LikeStoryCommand) {
        eventStream.publish(LIKE_STORY_COMMAND, cmd)
    }

    fun unlike(cmd: UnlikeStoryCommand) {
        eventStream.publish(UNLIKE_STORY_COMMAND, cmd)
    }
}
