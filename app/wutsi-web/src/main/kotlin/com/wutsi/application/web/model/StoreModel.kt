package com.wutsi.application.web.model

data class StoreModel(
    public val id: Long = -1,
    public val status: String = "",
    public val productCount: Int = 0,
    public val cancellationPolicy: CancellationPolicyModel = CancellationPolicyModel(),
    public val returnPolicy: ReturnPolicyModel = ReturnPolicyModel(),
)
