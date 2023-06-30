package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.ViewEntity

interface ViewRepository {
    fun save(view: ViewEntity)
    fun findStoryIdsByUserIdOrDeviceId(userId: Long?, deviceId: String): List<Long>
}
