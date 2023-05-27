package com.wutsi.blog.comment.dao

import com.wutsi.blog.comment.domain.Comment
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("")
@Repository
interface CommentV0Repository : CrudRepository<Comment, Long> {
    fun findByStoryId(storyId: Long): List<Comment>
}
