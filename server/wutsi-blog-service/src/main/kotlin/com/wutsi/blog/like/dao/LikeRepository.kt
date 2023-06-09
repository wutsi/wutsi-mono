package com.wutsi.blog.like.dao

import com.wutsi.blog.like.domain.LikeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CrudRepository<LikeEntity, Long> {
    fun findByStoryIdAndUserId(storyId: Long, userId: Long): LikeEntity?
    fun findByStoryIdAndDeviceId(storyId: Long, deviceId: String): LikeEntity?

    fun findByStoryIdInAndUserId(storyId: List<Long>, userId: Long): List<LikeEntity>
    fun findByStoryIdInAndDeviceId(storyId: List<Long>, deviceId: String): List<LikeEntity>
}
