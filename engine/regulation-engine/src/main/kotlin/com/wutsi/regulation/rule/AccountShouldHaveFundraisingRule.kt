package com.wutsi.regulation.rule

import com.wutsi.error.ErrorURN
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.regulation.Rule

class AccountShouldHaveFundraisingRule(private val account: Account) : Rule {
    override fun check() {
        if (account.storeId == null) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.NO_FUNDRAISING.urn,
                    data = mapOf(
                        "account-id" to account.id,
                    ),
                ),
            )
        }
    }
}
