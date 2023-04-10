package com.wutsi.regulation.rule

import com.wutsi.enums.StoreStatus
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.Fundraising
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.regulation.Rule

class FundraisingShouldBeActiveRule(private val fundraising: Fundraising) : Rule {
    override fun check() {
        if (fundraising.status != StoreStatus.ACTIVE.name) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.FUNDRAISING_NOT_ACTIVE.urn,
                    data = mapOf(
                        "store-id" to fundraising.id,
                    ),
                ),
            )
        }
    }
}
