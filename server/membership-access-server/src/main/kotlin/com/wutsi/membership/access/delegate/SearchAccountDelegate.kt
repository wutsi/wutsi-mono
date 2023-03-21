package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.access.dto.SearchAccountResponse
import com.wutsi.membership.access.service.AccountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
public class SearchAccountDelegate(
    private val service: AccountService,
    private val httpRequest: HttpServletRequest,
    private val logger: KVLogger,
) {
    public fun invoke(request: SearchAccountRequest): SearchAccountResponse {
        logger.add("request_status", request.status)
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_account_ids", request.accountIds)
        logger.add("request_business", request.business)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val language = httpRequest.getHeader("Accept-Language")
        val accounts = service.search(request)
        logger.add("count", accounts.size)
        return SearchAccountResponse(
            accounts = accounts.map { service.toAccountSummary(it, language) },
        )
    }
}
