package com.wutsi.blog.story.dao.impl

import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ViewEntity
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class ViewRepositoryCache(
    private val cache: Cache,
) : ViewRepository {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewRepositoryCache::class.java)
    }

    override fun save(view: ViewEntity) {
        val key = getKey(view.userId, view.deviceId)
        try {
            val value = cache.get(key, String::class.java)
            if (value == null) {
                cache.put(key, view.storyId.toString())
            } else {
                val storyId = view.storyId.toString()
                val storyIds = value.split(",").toMutableList()
                if (!storyIds.contains(storyId)) {
                    storyIds.add(storyId)
                    cache.put(key, storyIds.joinToString(","))
                }
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected cache error. key=$key", ex)
        }
    }

    override fun findStoryIdsByUserIdOrDeviceId(userId: Long?, deviceId: String): List<Long> {
        val key = getKey(userId, deviceId)
        return try {
            cache.get(key, String::class.java)?.split(",")?.map { it.toLong() }?.reversed()
                ?: emptyList()
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected cache error. key=$key", ex)
            emptyList()
        }
    }

    private fun getKey(userId: Long?, deviceId: String): String =
        if (userId != null) {
            "urn:cache:views:user:$userId"
        } else {
            "urn:cache:views:device:$deviceId"
        }
}
