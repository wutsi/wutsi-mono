package com.wutsi.platform.payment.provider.paypal.model

data class PPCapture(
    val id: String = "",
    val status: String = "",
    val amount: PPMoney = PPMoney(),
    val final_capture: Boolean = false,
    val seller_receivable_breakdown: PPSellerReceivableBreakdown = PPSellerReceivableBreakdown(),
    val links: List<PPLink> = emptyList(),
    val create_time: String = "",
    val update_time: String = "",
)