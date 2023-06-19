package com.wutsi.blog.account.migration

import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.blog.account.service.LoginService
import com.wutsi.blog.event.EventType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class LoginMigrator(private val service: LoginService) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun migrate(session: SessionEntity) {
        login(session)
        logout(session)
    }

    private fun login(session: SessionEntity) {
        service.notify(
            type = EventType.USER_LOGGED_IN_EVENT,
            accessToken = session.accessToken,
            userId = session.account.user.id!!,
            timestamp = session.loginDateTime.time
        )
    }

    private fun logout(session: SessionEntity) {
        session.logoutDateTime?.let {
            service.notify(
                type = EventType.USER_LOGGED_IN_EVENT,
                accessToken = session.accessToken,
                userId = session.account.user.id!!,
                timestamp = it.time
            )
        }
    }
}
