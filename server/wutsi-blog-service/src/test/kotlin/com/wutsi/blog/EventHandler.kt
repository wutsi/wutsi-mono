package com.wutsi.blog

import com.wutsi.blog.client.event.ChannelBoundedEvent
import com.wutsi.blog.client.event.ChannelUnboundedEvent
import com.wutsi.blog.client.event.LoginEvent
import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.event.UpdateUserEvent
import com.wutsi.blog.client.event.ViewEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler {
    var loginEvent: LoginEvent? = null
    var viewEvent: ViewEvent? = null
    var publishEvent: PublishEvent? = null
    var updateUserEvent: UpdateUserEvent? = null
    var channelBoundedEvent: ChannelBoundedEvent? = null
    var channelUnboundedEvent: ChannelUnboundedEvent? = null

    fun init() {
        loginEvent = null
        viewEvent = null
        publishEvent = null
        updateUserEvent = null
        channelBoundedEvent = null
        channelUnboundedEvent = null
    }

    @EventListener
    fun onUpdateUserEvent(event: UpdateUserEvent) {
        updateUserEvent = event
    }

    @EventListener
    fun onLoginEvent(event: LoginEvent) {
        this.loginEvent = event
    }

    @EventListener
    fun onViewEvent(event: ViewEvent) {
        this.viewEvent = event
    }

    @EventListener
    fun onPublish(event: PublishEvent) {
        this.publishEvent = event
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
