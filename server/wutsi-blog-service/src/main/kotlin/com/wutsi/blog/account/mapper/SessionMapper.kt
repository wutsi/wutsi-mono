package com.wutsi.blog.account.mapper

import com.wutsi.blog.account.domain.Session
import com.wutsi.blog.client.user.SessionDto
import org.springframework.stereotype.Service

@Service
class SessionMapper {
    fun toSessionDto(session: Session) = SessionDto(
        accountId = session.account.id!!,
        userId = session.account.user.id!!,
        runAsUserId = session.runAsUser?.id,
        accessToken = session.accessToken,
        refreshToken = session.refreshToken,
        loginDateTime = session.loginDateTime,
        logoutDateTime = session.logoutDateTime,

    )
}
