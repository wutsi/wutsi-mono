package com.wutsi.blog.follower.dao

import com.wutsi.blog.follower.domain.SubscriptionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : CrudRepository<SubscriptionEntity, Long> {
    fun findByUserIdAndSubscriberId(userId: Long, subscriberId: Long): SubscriptionEntity?
    fun countByUserId(userId: Long): Long
}
