package com.wutsi.membership.manager.delegate

import com.wutsi.membership.access.MembershipAccessApi
import org.springframework.stereotype.Service

@Service
class ImportCategoryDelegate(private val membershipAccessApi: MembershipAccessApi) {
    fun invoke(language: String) {
        membershipAccessApi.importCategory(language)
    }
}
