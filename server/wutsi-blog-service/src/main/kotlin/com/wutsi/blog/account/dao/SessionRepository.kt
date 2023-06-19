package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.SessionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SessionRepository : CrudRepository<SessionEntity, Long> {
    fun findByAccessToken(token: String): Optional<SessionEntity>
}
