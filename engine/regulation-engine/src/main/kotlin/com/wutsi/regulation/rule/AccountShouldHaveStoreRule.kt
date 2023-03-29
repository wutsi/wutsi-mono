package com.wutsi.regulation.rule

import com.wutsi.error.ErrorURN
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.Error
import com.wutsi.regulation.Rule

class AccountShouldHaveStoreRule(private val account: Account) : Rule {
    override fun check() {
        if (account.storeId == null) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.NO_STORE.urn,
                    data = mapOf(
                        "account-id" to account.id,
                    ),
                ),
            )
        }
    }
}
