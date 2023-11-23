package com.wutsi.blog.like.dao

import com.wutsi.blog.like.domain.LikeEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface LikeRepository : CrudRepository<LikeEntity, Long> {
    fun findByStoryIdAndUserId(storyId: Long, userId: Long): LikeEntity?
    fun findByStoryIdAndDeviceId(storyId: Long, deviceId: String): LikeEntity?

    fun findByStoryIdInAndUserId(storyId: List<Long>, userId: Long): List<LikeEntity>
    fun findByStoryIdInAndDeviceId(storyId: List<Long>, deviceId: String): List<LikeEntity>

    fun findByUserId(userId: Long, page: Pageable): List<LikeEntity>

    fun findByDeviceId(deviceId: String, page: Pageable): List<LikeEntity>

    fun findAll(page: Pageable): List<LikeEntity>

    fun findByTimestampBetween(start: Date, end: Date): List<LikeEntity>
}
