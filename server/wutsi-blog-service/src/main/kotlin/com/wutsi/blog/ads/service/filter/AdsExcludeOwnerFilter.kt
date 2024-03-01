package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter

class AdsExcludeOwnerFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>): List<AdsEntity> {
        return ads.filter { ad -> ad.userId != request.impressionContext?.blogId }
    }
}