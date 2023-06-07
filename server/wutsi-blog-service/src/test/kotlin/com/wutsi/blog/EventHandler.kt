package com.wutsi.blog

import com.wutsi.blog.client.event.ChannelBoundedEvent
import com.wutsi.blog.client.event.ChannelUnboundedEvent
import com.wutsi.blog.client.event.LoginEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler {
    var loginEvent: LoginEvent? = null
    var channelBoundedEvent: ChannelBoundedEvent? = null
    var channelUnboundedEvent: ChannelUnboundedEvent? = null

    fun init() {
        loginEvent = null
        channelBoundedEvent = null
        channelUnboundedEvent = null
    }

    @EventListener
    fun onLoginEvent(event: LoginEvent) {
        this.loginEvent = event
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
