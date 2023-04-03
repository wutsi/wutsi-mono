package com.wutsi.application.web.model

data class FundraisingModel(
    val id: Long = -1,
    val baseAmountValue: Long = 0,
    val currency: String? = null,
    val baseAmount: String = "",
)
