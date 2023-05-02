package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.Session
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SessionRepository : CrudRepository<Session, Long> {
    fun findByAccessToken(token: String): Optional<Session>
}
