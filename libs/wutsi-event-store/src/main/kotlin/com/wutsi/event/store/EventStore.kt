package com.wutsi.event.store

interface EventStore {
    fun store(event: Event): String
    fun event(id: String): Event
    fun events(streamId: Long, entityId: String? = null): List<Event>
}
