package com.wutsi.regulation.rule

import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.Error
import com.wutsi.regulation.Rule

class AccountShouldBeOwnerOfStoreRule(private val account: Account, private val store: Store) : Rule {
    override fun check() {
        if (account.id != store.accountId) {
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.STORE_NOT_OWNER.urn,
                    data = mapOf(
                        "account-id" to account.id,
                        "store-id" to store.id,
                    ),
                ),
            )
        }
    }
}
