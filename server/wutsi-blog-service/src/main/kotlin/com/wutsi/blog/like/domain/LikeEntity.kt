package com.wutsi.blog.like.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_LIKE_V2")
class LikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long? = null,

    val deviceId: String? = null,

    @Column(name = "story_fk")
    val storyId: Long = 0,

    val timestamp: Date = Date(),
)
