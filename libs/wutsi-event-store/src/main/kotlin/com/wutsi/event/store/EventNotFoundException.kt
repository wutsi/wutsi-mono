package com.wutsi.event.store

data class EventNotFoundException(val eventId: String) : Exception() {
    override val message: String
        get() = "event-id=$eventId"
}
