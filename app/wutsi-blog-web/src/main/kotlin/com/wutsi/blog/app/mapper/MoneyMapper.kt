package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.country.dto.Country
import org.springframework.stereotype.Service

@Service
class MoneyMapper {
    fun toMoneyModel(amount: Long, currency: String) = MoneyModel(
        value = amount,
        currency = currency,
        text = formatMoney(amount, currency)
    )

    private fun formatMoney(amount: Long, currency: String): String {
        val country = Country.all.find {
            currency.equals(it.currency, true) ||
                    currency.equals(it.internationalCurrency, true)
        }
        return if (country != null) {
            if (currency.equals(country.currency, true)) {
                country.createMoneyFormat().format(amount)
            } else {
                country.createInternationalMoneyFormat().format(amount)
            }
        } else {
            "$amount $currency"
        }
    }
}
