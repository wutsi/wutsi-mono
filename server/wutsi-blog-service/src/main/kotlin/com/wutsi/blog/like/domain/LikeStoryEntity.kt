package com.wutsi.blog.like.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_LIKE_STORY")
class LikeStoryEntity(
    @Id
    @Column(name = "story_fk")
    val storyId: Long = -1,

    var count: Long = 0,
)
