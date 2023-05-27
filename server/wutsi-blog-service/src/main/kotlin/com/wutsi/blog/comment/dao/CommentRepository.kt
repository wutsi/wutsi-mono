package com.wutsi.blog.comment.dao

import com.wutsi.blog.comment.domain.CommentEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CrudRepository<CommentEntity, Long> {
    fun findByStoryIdAndUserId(storyId: Long, userId: Long): CommentEntity?
    fun findByStoryIdInAndUserId(storyId: List<Long>, userId: Long): List<CommentEntity>
}
