package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.Story
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : CrudRepository<Story, Long> {
    fun findBySourceUrlHash(sourceUrlHash: String): List<Story>
}
