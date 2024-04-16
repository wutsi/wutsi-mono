package com.wutsi.blog.mail.service.model

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsType

data class AdsModel(
    val id: String = "",
    val userId: Long = -1,
    val title: String = "",
    val imageUrl: String? = null,
    val url: String? = null,
    val type: AdsType = AdsType.UNKNOWN,
    val ctaType: AdsCTAType = AdsCTAType.UNKNOWN,
    val ctaUrl: String? = null,
)
