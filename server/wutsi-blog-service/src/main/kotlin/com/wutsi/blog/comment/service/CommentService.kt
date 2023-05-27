package com.wutsi.blog.comment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val objectMapper: ObjectMapper,
)
