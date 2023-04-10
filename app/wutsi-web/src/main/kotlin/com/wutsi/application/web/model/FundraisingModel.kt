package com.wutsi.application.web.model

data class FundraisingModel(
    val id: Long = -1,
    val amount: Long = 0,
    val currency: String? = null,
    val baseAmount: String = "",
    val description: String? = null,
    val videoUrl: String? = null,
)
