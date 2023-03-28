package com.wutsi.membership.manager.delegate

import com.wutsi.membership.access.MembershipAccessApi
import org.springframework.stereotype.Service

@Service
class ImportPlaceDelegate(private val membershipAccessApi: MembershipAccessApi) {
    fun invoke(country: String) {
        membershipAccessApi.importPlace(country)
    }
}
