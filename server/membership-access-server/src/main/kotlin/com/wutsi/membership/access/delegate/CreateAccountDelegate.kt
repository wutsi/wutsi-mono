package com.wutsi.membership.access.delegate

import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import com.wutsi.membership.access.service.AccountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateAccountDelegate(
    private val service: AccountService,
    private val logger: KVLogger,
) {
    @Transactional
    public fun invoke(request: CreateAccountRequest): CreateAccountResponse {
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_picture_url", request.pictureUrl)
        logger.add("request_country", request.country)
        logger.add("request_language", request.language)
        logger.add("request_city_id", request.cityId)
        logger.add("request_display_name", request.displayName)

        val account = service.create(request)
        logger.add("account_id", account.id)

        return CreateAccountResponse(
            accountId = account.id ?: -1,
        )
    }
}
