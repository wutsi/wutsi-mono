package com.wutsi.blog.subscription.dao

import com.wutsi.blog.subscription.domain.SubscriptionUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionUserRepository : CrudRepository<SubscriptionUserEntity, Long>
