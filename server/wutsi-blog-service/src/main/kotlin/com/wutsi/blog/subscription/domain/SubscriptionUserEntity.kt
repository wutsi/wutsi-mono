package com.wutsi.blog.subscription.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_SUBSCRIPTION_USER")
data class SubscriptionUserEntity(
    @Id
    @Column(name = "user_fk")
    val userId: Long = -1,

    var count: Long = 0,
)
