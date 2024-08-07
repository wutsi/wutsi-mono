package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsImpressionFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        val filtered = ads
            .sortedBy { ad -> ad.todayImpressions }
            .groupBy { ad -> ad.type }

        return AdsType.entries
            .mapNotNull { type -> filtered[type]?.take(request.impressionContext!!.adsPerType) }
            .flatten()
    }
}