package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : CrudRepository<StoryEntity, Long> {
    fun findBySourceUrlHash(sourceUrlHash: String): List<StoryEntity>
    fun findBySourceUrlNotNull(): List<StoryEntity>
    fun findByStatus(status: StoryStatus): List<StoryEntity>
    fun countByUserIdAndStatusAndDeleted(userId: Long, status: StoryStatus, deleted: Boolean): Long

    @Query("SELECT SUM(S.readCount) FROM StoryEntity S WHERE S.userId=?1")
    fun sumReadCountByUserId(userId: Long): Long?

    @Query("SELECT AVG(S.clickCount) FROM StoryEntity S WHERE S.userId=?1")
    fun averageClickCountByUserId(userId: Long): Long?

    @Query("SELECT SUM(S.totalDurationSeconds) FROM StoryEntity S WHERE S.userId=?1")
    fun sumTotalDurationSecondsByUserId(userId: Long): Long?

    @Query("SELECT S.userId FROM StoryEntity S WHERE S.id IN ?1")
    fun findUserIdsByIds(ids: List<Long>): List<Long>

    @Query("SELECT S.id FROM StoryEntity S WHERE S.userId IN ?1")
    fun findIdsByUserIds(userIds: List<Long>, pagination: Pageable): List<Long>
}
