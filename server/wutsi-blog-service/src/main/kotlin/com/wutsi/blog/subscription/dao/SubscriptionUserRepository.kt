package com.wutsi.blog.follower.dao

import com.wutsi.blog.follower.domain.SubscriptionUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionUserRepository : CrudRepository<SubscriptionUserEntity, Long>
