package com.wutsi.blog.user.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.BLOG_CREATED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UserEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: UserService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(BLOG_CREATED_EVENT, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            BLOG_CREATED_EVENT -> service.onBlogCreated(
                objectMapper.readValue(
                    event.payload,
                    EventPayload::class.java,
                ),
            )

            else -> {}
        }
    }
}
