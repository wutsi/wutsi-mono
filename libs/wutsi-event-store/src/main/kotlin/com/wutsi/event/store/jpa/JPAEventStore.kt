package com.wutsi.event.store.jpa

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventNotFoundException
import com.wutsi.event.store.EventStore
import com.wutsi.event.store.PayloadDeserializer
import org.apache.commons.text.StringEscapeUtils
import javax.persistence.EntityManager
import javax.persistence.Query

class JPAEventStore(
    private val em: EntityManager,
    private val objectMapper: ObjectMapper,
    private val deserializer: PayloadDeserializer,
) : EventStore {
    override fun store(event: Event): String {
        val entity = EventEntity(
            id = event.id,
            streamId = event.streamId,
            entityId = event.entityId,
            userId = event.userId,
            deviceId = event.deviceId,
            version = getCurrentVersion(event.streamId) + 1,
            type = event.type,
            timestamp = event.timestamp,
            payload = event.payload?.let { StringEscapeUtils.escapeJson(objectMapper.writeValueAsString(it)) },
            metadata = event.metadata?.let { StringEscapeUtils.escapeJson(objectMapper.writeValueAsString(it)) },
        )
        em.persist(entity)
        return entity.id
    }

    override fun event(id: String): Event {
        val event = em.find(EventEntity::class.java, id) ?: throw EventNotFoundException(id)
        return toEvent(event)
    }

    override fun events(
        streamId: Long,
        entityId: String?,
        type: String?,
        userId: String?,
        deviceId: String?,
    ): List<Event> =
        (
            createQueryCount(
                false,
                streamId,
                entityId,
                type,
                userId,
                deviceId,
            ).resultList as List<EventEntity>
            ).map { toEvent(it) }

    override fun eventCount(
        streamId: Long,
        entityId: String?,
        type: String?,
        userId: String?,
        deviceId: String?,
    ): Long =
        createQueryCount(
            true,
            streamId,
            entityId,
            type,
            userId,
            deviceId,
        ).singleResult as Long

    private fun createQueryCount(
        count: Boolean,
        streamId: Long,
        entityId: String?,
        type: String?,
        userId: String?,
        deviceId: String?,
    ): Query {
        // Select
        var jql = if (count) {
            "SELECT COUNT(*)"
        } else {
            "SELECT e"
        }

        // From
        jql += " FROM EventEntity e"

        // Where
        jql += " WHERE e.streamId = :stream_id"
        entityId?.let { jql += " AND e.entityId = :entity_id" }
        userId?.let { jql += " AND e.userId = :user_id" }
        deviceId?.let { jql += " AND e.deviceId = :device_id" }
        type?.let { jql += " AND e.type = :type" }

        // Order
        jql += " ORDER BY e.version ASC"

        // Parameters
        val query = em.createQuery(jql)
        query.setParameter("stream_id", streamId)
        entityId?.let { query.setParameter("entity_id", it) }
        userId?.let { query.setParameter("user_id", it) }
        deviceId?.let { query.setParameter("device_id", it) }
        type?.let { query.setParameter("type", it) }

        return query
    }

    override fun events(ids: List<String>): List<Event> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        // Query
        val jql = "SELECT e FROM EventEntity e WHERE e.id in :ids"
        val query = em.createQuery(jql)

        // Parameters
        query.setParameter("ids", ids)

        // Result
        return (query.resultList as List<EventEntity>).map { toEvent(it) }
    }

    private fun toEvent(event: EventEntity) = Event(
        id = event.id,
        streamId = event.streamId,
        type = event.type,
        entityId = event.entityId,
        userId = event.userId,
        deviceId = event.deviceId,
        payload = event.payload?.let {
            deserializer.deserialize(
                event.type,
                StringEscapeUtils.unescapeJson(it),
            )
        },
        metadata = event.metadata?.let {
            objectMapper.readValue(
                StringEscapeUtils.unescapeJson(it),
                Map::class.java,
            ) as Map<String, Any?>
        },
        timestamp = event.timestamp,
    )

    private fun getCurrentVersion(streamId: Long): Long {
        val result = em.createQuery("SELECT MAX(e.version) FROM EventEntity e WHERE e.streamId=:stream_id")
            .setParameter("stream_id", streamId)
            .resultList
        return if (result.isEmpty() || result[0] == null) {
            0
        } else {
            (result[0] as Number).toLong()
        }
    }
}
