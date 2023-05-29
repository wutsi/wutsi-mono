package com.wutsi.blog.subscription.dao

import com.wutsi.blog.subscription.domain.Follower
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("")
@Repository
interface FollowerRepository : CrudRepository<Follower, Long> {
    fun findByUserId(userId: Long): List<Follower>
}
