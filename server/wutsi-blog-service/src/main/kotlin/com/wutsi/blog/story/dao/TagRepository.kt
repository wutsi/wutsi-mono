package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.TagEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : CrudRepository<TagEntity, Long> {
    fun findByNameIn(names: List<String>): List<TagEntity>
    fun findByNameStartsWithOrderByTotalStoriesDesc(name: String): List<TagEntity>
}
