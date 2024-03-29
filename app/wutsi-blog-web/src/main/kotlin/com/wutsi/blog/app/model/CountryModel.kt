package com.wutsi.blog.app.model

data class CountryModel(
    val code: String = "",
    val name: String = "",
    val currencyCode: String = "",
    val currencySymbol: String = "",
    val currencyDisplayName: String = "",
    val monetaryFormat: String = "",
    val flagUrl: String = "",
    val paymentProviderTypes: List<PaymentProviderTypeModel> = emptyList(),
    val internationalCurrency: String = "",
    val minDailyAdsBudget: Long = 0,
)
