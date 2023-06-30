package com.wutsi.blog.story.service.impl

import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.blog.story.service.ViewService
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service

@Service
class ViewServiceCache(
    private val cache: Cache,
    private val tracingContext: TracingContext,
) : ViewService {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewServiceCache::class.java)
    }

    override fun view(payload: ViewStoryCommand) {
        val key = getKey(payload.userId, payload.deviceId)
        try {
            val value = cache.get(key, String::class.java)
            if (value == null) {
                cache.put(key, payload.storyId.toString())
            } else {
                val storyId = payload.storyId.toString()
                val storyIds = value.split(",").toMutableSet()
                if (!storyIds.contains(storyId)) {
                    storyIds.add(storyId)
                    cache.put(key, storyIds.joinToString(","))
                }
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected cache error. key=$key", ex)
        }
    }

    override fun getViews(userId: Long?): List<Long> {
        val key = getKey(userId, tracingContext.deviceId())
        return try {
            cache.get(key, String::class.java)?.split(",")?.map { it.toLong() }
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
