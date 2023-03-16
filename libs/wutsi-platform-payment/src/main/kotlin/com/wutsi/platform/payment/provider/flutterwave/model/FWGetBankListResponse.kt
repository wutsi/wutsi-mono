package com.wutsi.platform.payment.provider.flutterwave.model

data class FWGetBankListResponse(
    val data: List<FWBank> = emptyList(),
)
