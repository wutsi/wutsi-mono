package com.wutsi.application.marketplace.settings.product.dto

public data class SubmitProductEventRequest(
    public val online: Boolean = false,
    public val meetingId: String? = null,
    public val meetingPassword: String? = null,
    public val meetingProviderId: Long? = null,
    public val startDate: String = "",
    public val startTime: String = "",
    public val endTime: String = "",
)
