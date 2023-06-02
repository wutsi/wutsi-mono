package com.wutsi.blog.user.dao

import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserEntityRepository : CrudRepository<UserEntity, Long> {
    fun findByEmailIgnoreCase(email: String): Optional<UserEntity>
    fun findByNameIgnoreCase(name: String): Optional<UserEntity>
}
