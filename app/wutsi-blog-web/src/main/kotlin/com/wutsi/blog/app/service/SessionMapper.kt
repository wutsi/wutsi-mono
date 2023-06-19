package com.wutsi.blog.app.service

import com.wutsi.blog.account.dto.Session
import com.wutsi.blog.app.model.SessionModel
import org.springframework.stereotype.Service

@Service
class SessionMapper {
    fun toSessionModel(session: Session) = SessionModel(
        accessToken = session.accessToken,
        refreshToken = session.refreshToken,
        logoutDateTime = session.logoutDateTime,
        loginDateTime = session.loginDateTime,
        accountId = session.accountId,
        userId = session.userId,
        runAsUserId = session.runAsUserId,
    )
}
