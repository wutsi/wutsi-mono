package com.wutsi.blog.event

import com.wutsi.platform.core.stream.Event

interface EventHandler {
    fun handle(event: Event)
}
