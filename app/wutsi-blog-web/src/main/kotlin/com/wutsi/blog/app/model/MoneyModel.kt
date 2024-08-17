package com.wutsi.blog.app.model

import com.wutsi.blog.country.dto.Country

data class MoneyModel(
    val value: Long = 0,
    val currency: String = "",
    val text: String = "",
) {
    val free: Boolean
        get() = (value == 0L)

    val currencySymbol: String
        get() = Country.all.find { it -> it.currency.equals(currency, false) }?.currencySymbol ?: currency

    override fun toString(): String {
        return text
    }
}
