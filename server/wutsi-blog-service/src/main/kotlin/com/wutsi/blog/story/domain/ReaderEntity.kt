package com.wutsi.blog.story.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

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
    var email: Boolean = false,
    var creationDateTime: Date = Date(),
)
