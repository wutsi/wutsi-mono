package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
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
}
