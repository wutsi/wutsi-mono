package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class MailBackend(private val eventStream: EventStream) {
    fun sendDaily(command: SendStoryDailyEmailCommand) {
        return eventStream.publish(EventType.SEND_STORY_DAILY_EMAIL_COMMAND, command)
    }
}
