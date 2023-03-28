package com.wutsi.membership.manager.delegate

import com.wutsi.error.ErrorURN
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import feign.FeignException
import org.springframework.stereotype.Service

@Service
public class GetMemberDelegate(private val membershipAccessApi: MembershipAccessApi) : AbstractGetMemberDelegate() {
    public fun invoke(id: Long): GetMemberResponse {
        try {
            val account = membershipAccessApi.getAccount(id).account
            return GetMemberResponse(
                member = toMember(account)
            )
        } catch (ex: FeignException.NotFound) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_FOUND.urn,
                ),
            )
        }
    }
}
