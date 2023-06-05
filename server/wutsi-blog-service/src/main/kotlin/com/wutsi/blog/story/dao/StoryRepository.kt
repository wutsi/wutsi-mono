package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.story.dto.StoryStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : CrudRepository<Story, Long> {
    fun findBySourceUrlHash(sourceUrlHash: String): List<Story>
    fun findBySourceUrlNotNull(): List<Story>
    fun findByStatus(status: StoryStatus): List<Story>
}
