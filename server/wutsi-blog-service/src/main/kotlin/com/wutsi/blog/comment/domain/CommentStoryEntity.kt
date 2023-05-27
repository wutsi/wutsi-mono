package com.wutsi.blog.comment.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_COMMENT_STORY")
class CommentStoryEntity(
    @Id
    @Column(name = "story_fk")
    val storyId: Long = -1,

    var count: Long = 0,
)
