package com.wutsi.platform.payment.model

import com.wutsi.platform.payment.Capability

data class GetCapabilitiesResponse(
    val capabilities: List<Capability> = emptyList(),
)
