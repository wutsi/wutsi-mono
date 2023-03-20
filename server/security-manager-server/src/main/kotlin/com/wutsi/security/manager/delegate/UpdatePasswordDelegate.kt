package com.wutsi.security.manager.delegate

import com.wutsi.security.manager.dto.UpdatePasswordRequest
import com.wutsi.security.manager.service.PasswordService
import com.wutsi.security.manager.util.SecurityUtil
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class UpdatePasswordDelegate(private val service: PasswordService) {
    @Transactional
    public fun invoke(request: UpdatePasswordRequest) {
        val accountId = SecurityUtil.getAccountId()
        service.update(accountId, request)
    }
}
