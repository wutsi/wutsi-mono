package com.wutsi.membership.manager.delegate

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchMemberDelegate(
    private val membershipAccessApi: MembershipAccessApi,
    private val logger: KVLogger,
) {
    public fun invoke(request: SearchMemberRequest): SearchMemberResponse {
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_business", request.business)
        logger.add("request_store", request.store)
        logger.add("request_city_id", request.cityId)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val accounts = membershipAccessApi.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = request.phoneNumber,
                status = AccountStatus.ACTIVE.name,
                business = request.business,
                store = request.store,
                limit = request.limit,
                offset = request.offset,
                cityId = request.cityId,
            ),
        ).accounts

        return SearchMemberResponse(
            members = accounts.map {
                MemberSummary(
                    id = it.id,
                    displayName = it.displayName,
                    pictureUrl = it.pictureUrl,
                    categoryId = it.categoryId,
                    business = it.business,
                    country = it.country,
                    cityId = it.cityId,
                    language = it.language,
                    active = it.status == AccountStatus.ACTIVE.name,
                    superUser = it.superUser,
                    name = it.name,
                )
            },
        )
    }
}
