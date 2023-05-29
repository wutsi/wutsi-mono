package com.wutsi.blog.subscription.domain

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Deprecated("")
@Entity
@Table(name = "T_FOLLOWER")
data class Follower(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    @Column(name = "follower_user_fk")
    val followerUserId: Long = -1,

    val followDateTime: Date = Date(),
)
