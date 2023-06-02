package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmailIgnoreCase(email: String): Optional<User>
    fun findByNameIgnoreCase(name: String): Optional<User>
    fun findByAutoFollowByBlogs(autoFollowByBlogs: Boolean): List<User>
}
