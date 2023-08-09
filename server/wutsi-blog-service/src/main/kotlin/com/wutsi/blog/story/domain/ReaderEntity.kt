package com.wutsi.blog.story.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_READER")
data class ReaderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val userId: Long = -1,
    val storyId: Long = -1,
    var commented: Boolean = false,
    var liked: Boolean = false,
    var subscribed: Boolean = false,
)
