package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.service.AccountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
public class GetAccountByNameDelegate(
    private val service: AccountService,
    private val request: HttpServletRequest,
    private val logger: KVLogger,
) {
    public fun invoke(name: String): GetAccountResponse {
        logger.add("name", name)

        val language = request.getHeader("Accept-Language")
        val account = service.findByName(name, true)
        return GetAccountResponse(
            account = service.toAccount(account, language),
        )
    }
}
