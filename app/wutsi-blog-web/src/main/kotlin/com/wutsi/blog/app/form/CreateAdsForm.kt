package com.wutsi.blog.app.form

import com.wutsi.blog.ads.dto.AdsType

data class CreateAdsForm(
    val type: AdsType = AdsType.UNKNOWN,
    val title: String = "",
)
