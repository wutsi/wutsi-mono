package com.wutsi.blog.app.page.login.service

import com.wutsi.blog.app.page.login.model.SessionModel
import com.wutsi.blog.client.user.SessionDto
import org.springframework.stereotype.Service

@Service
class SessionMapper {
    fun toSessionModel(session: SessionDto) = SessionModel(
        accessToken = session.accessToken,
        refreshToken = session.refreshToken,
        logoutDateTime = session.logoutDateTime,
        loginDateTime = session.loginDateTime,
        accountId = session.accountId,
        userId = session.userId,
        runAsUserId = session.runAsUserId,
    )
}
