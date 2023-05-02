package com.wutsi.blog.story.service

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class ViewService(
    private val cache: Cache,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewService::class.java)
    }

    fun add(deviceId: String?, storyId: Long) {
        deviceId ?: return

        try {
            val deviceIds = get(storyId)
            if (deviceIds == null) {
                cache.put(key(storyId), mutableSetOf(deviceId))
            } else if (deviceIds.add(deviceId)) {
                cache.put(storyId, deviceIds)
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }
    }

    fun contains(deviceId: String?, storyId: Long): Boolean {
        deviceId ?: return false

        val views = get(storyId)
        return views?.contains(deviceId) ?: false
    }

    fun get(storyId: Long): MutableSet<String>? {
        return try {
            cache.get(key(storyId), MutableSet::class.java) as MutableSet<String>?
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
            null
        }
    }

    private fun key(storyId: Long) = "view-$storyId"
}
