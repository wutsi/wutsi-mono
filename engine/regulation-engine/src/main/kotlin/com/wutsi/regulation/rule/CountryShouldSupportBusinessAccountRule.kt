package com.wutsi.regulation.rule

import com.wutsi.error.ErrorURN
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.regulation.CountryNotSupportedException
import com.wutsi.regulation.RegulationEngine
import com.wutsi.regulation.Rule

class CountryShouldSupportBusinessAccountRule(
    private val account: Account,
    private val regulationEngine: RegulationEngine,
) : Rule {
    override fun check() {
        val country = account.country
        try {
            if (!regulationEngine.country(country).supportsBusinessAccount) {
                throw notSupported(country)
            }
        } catch (ex: CountryNotSupportedException) {
            throw notSupported(country, ex)
        }
    }

    private fun notSupported(country: String, cause: Throwable? = null) = ConflictException(
        error = Error(
            code = ErrorURN.BUSINESS_ACCOUNT_NOT_SUPPORTED_IN_COUNTRY.urn,
            data = mapOf(
                "account-id" to account.id,
                "country" to country,
            ),
        ),
        cause,
    )
}
