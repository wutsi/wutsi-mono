package com.wutsi.blog.comment.mapper

import com.wutsi.blog.client.comment.CommentCountDto
import com.wutsi.blog.client.comment.CommentDto
import com.wutsi.blog.comment.domain.Comment
import com.wutsi.blog.comment.domain.CommentCount
import org.springframework.stereotype.Service

@Service
class CommentMapper {
    fun toCommentDto(it: Comment) = CommentDto(
        id = it.id!!,
        userId = it.userId,
        storyId = it.storyId,
        modificationDateTime = it.modificationDateTime,
        text = it.text,
        creationDateTime = it.creationDateTime,
    )

    fun toCommentCountDto(it: CommentCount) = CommentCountDto(
        storyId = it.storyId,
        value = it.value,
    )
}
