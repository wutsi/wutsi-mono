package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : CrudRepository<StoryEntity, Long> {
    fun findBySourceUrlHash(sourceUrlHash: String): List<StoryEntity>
    fun findBySourceUrlNotNull(): List<StoryEntity>
    fun findByStatus(status: StoryStatus): List<StoryEntity>
}
