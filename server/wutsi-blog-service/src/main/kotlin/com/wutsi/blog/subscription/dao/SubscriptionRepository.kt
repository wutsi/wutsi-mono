package com.wutsi.blog.subscription.dao

import com.wutsi.blog.subscription.domain.SubscriptionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : CrudRepository<SubscriptionEntity, Long> {
    fun findByUserIdAndSubscriberId(userId: Long, subscriberId: Long): SubscriptionEntity?
    fun findByUserIdInAndSubscriberId(userId: List<Long>, subscriberId: Long): List<SubscriptionEntity>
    fun countByUserId(userId: Long): Long
}
