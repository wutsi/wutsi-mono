package com.wutsi.platform.payment.provider.paypal.model

data class PPSellerReceivableBreakdown(
    val gross_amount: PPMoney = PPMoney(),
    val paypal_fee: PPMoney = PPMoney(),
    val net_amount: PPMoney = PPMoney(),
    val receivable_amount: PPMoney = PPMoney(),
    val exchange_rate: PPExchangeRate = PPExchangeRate(),
)