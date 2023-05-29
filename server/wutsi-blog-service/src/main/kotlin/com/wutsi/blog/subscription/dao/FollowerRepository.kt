package com.wutsi.blog.follower.dao

import com.wutsi.blog.follower.domain.Follower
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Deprecated("")
@Repository
interface FollowerRepository : CrudRepository<Follower, Long> {
    fun findByUserId(userId: Long): List<Follower>
}
