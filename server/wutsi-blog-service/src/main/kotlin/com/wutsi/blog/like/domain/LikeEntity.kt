package com.wutsi.blog.like.domain

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_LIKE")
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
