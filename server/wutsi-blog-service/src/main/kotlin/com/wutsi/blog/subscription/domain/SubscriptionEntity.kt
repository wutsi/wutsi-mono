package com.wutsi.blog.subscription.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_SUBSCRIPTION")
data class SubscriptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "subscriber_fk")
    val subscriberId: Long = -1,

    @Column(name = "story_fk")
    val storyId: Long? = null,

    val referer: String? = null,

    val timestamp: Date = Date(),
)
