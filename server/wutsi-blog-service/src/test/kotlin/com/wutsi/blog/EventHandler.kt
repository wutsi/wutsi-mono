package com.wutsi.blog

import com.wutsi.blog.client.event.ChannelBoundedEvent
import com.wutsi.blog.client.event.ChannelUnboundedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Deprecated("")
@Service
class EventHandler {
    var channelBoundedEvent: ChannelBoundedEvent? = null
    var channelUnboundedEvent: ChannelUnboundedEvent? = null

    fun init() {
        channelBoundedEvent = null
        channelUnboundedEvent = null
    }

    @EventListener
    fun onChannelBounded(event: ChannelBoundedEvent) {
        this.channelBoundedEvent = event
    }

    @EventListener
    fun onChannelUnbounded(event: ChannelUnboundedEvent) {
        this.channelUnboundedEvent = event
    }
}
