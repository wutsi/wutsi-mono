package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.SHARE_STORY_COMMAND
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class ShareBackend(
    private val eventStream: EventStream,
) {
    fun share(cmd: ShareStoryCommand) {
        eventStream.publish(SHARE_STORY_COMMAND, cmd)
    }
}
