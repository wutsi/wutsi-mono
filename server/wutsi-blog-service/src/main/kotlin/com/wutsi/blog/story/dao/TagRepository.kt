package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.Tag
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : CrudRepository<Tag, Long> {
    fun findByNameIn(names: List<String>): List<Tag>
    fun findByNameStartsWithOrderByTotalStoriesDesc(name: String): List<Tag>
}
