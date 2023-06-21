package com.wutsi.blog.account.dao

import com.wutsi.blog.account.domain.SessionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.Optional

@Repository
interface SessionRepository : CrudRepository<SessionEntity, Long> {
    fun findByAccessToken(token: String): Optional<SessionEntity>
    fun findByLoginDateTimeLessThanAndLogoutDateTimeNull(loginDateTime: Date, pagination: Pageable): List<SessionEntity>
}
