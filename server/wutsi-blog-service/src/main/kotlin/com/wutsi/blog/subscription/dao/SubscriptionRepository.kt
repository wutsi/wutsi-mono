package com.wutsi.blog.subscription.dao

import com.wutsi.blog.subscription.domain.SubscriptionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : CrudRepository<SubscriptionEntity, Long> {
    fun findByUserIdAndSubscriberId(userId: Long, subscriberId: Long): SubscriptionEntity?
    fun findByUserIdInAndSubscriberId(userId: List<Long>, subscriberId: Long): List<SubscriptionEntity>
    fun findByUserIdIn(userId: List<Long>): List<SubscriptionEntity>
    fun findBySubscriberId(subscriberId: Long): List<SubscriptionEntity>

    fun findByUserId(userId: Long, pageable: Pageable): List<SubscriptionEntity>
}
