package com.wutsi.blog.share.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_SHARE_STORY")
class ShareStoryEntity(
    @Id
    @Column(name = "story_fk")
    var storyId: Long = -1,

    var count: Long = 0,
)
