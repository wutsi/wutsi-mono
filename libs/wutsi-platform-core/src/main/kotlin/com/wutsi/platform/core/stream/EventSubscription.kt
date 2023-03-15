package com.wutsi.platform.core.stream

class EventSubscription(
    private val source: String,
    private val destination: EventStream,
) {
    init {
        destination.subscribeTo(source)
    }
}
