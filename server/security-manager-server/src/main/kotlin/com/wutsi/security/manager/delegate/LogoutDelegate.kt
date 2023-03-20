package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.service.LoginService
import com.wutsi.security.manager.util.SecurityUtil
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class LogoutDelegate(
    private val service: LoginService,
) {
    @Transactional
    fun invoke() {
        val accountId = SecurityUtil.getAccountId()
        service.logout(accountId)
    }
}
