package com.wutsi.event.store

interface EventStore {
    fun store(event: Event): String
    fun event(id: String): Event
    fun events(ids: List<String>): List<Event>
    fun events(
        streamId: Long,
        entityId: String? = null,
        type: String? = null,
        userId: String? = null,
        deviceId: String? = null,
    ): List<Event>

    fun eventCount(
        streamId: Long,
        entityId: String? = null,
        type: String? = null,
        userId: String? = null,
        deviceId: String? = null,
    ): Long
}
