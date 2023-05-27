package com.wutsi.blog.comment.dao

import com.wutsi.blog.comment.domain.CommentStoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentStoryRepository : CrudRepository<CommentStoryEntity, Long>
