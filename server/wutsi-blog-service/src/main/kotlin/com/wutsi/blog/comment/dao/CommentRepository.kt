package com.wutsi.blog.comment.dao

import com.wutsi.blog.comment.domain.CommentEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface CommentRepository : CrudRepository<CommentEntity, Long> {
    fun findByStoryIdInAndUserId(storyId: List<Long>, userId: Long): List<CommentEntity>
    fun findByStoryId(storyId: Long): List<CommentEntity>
    fun findByStoryId(storyId: Long, pagination: Pageable): List<CommentEntity>

    fun findByTimestampBetween(start: Date, end: Date): List<CommentEntity>
}
