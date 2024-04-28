package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsEmailFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        request.impressionContext?.email ?: return ads

        return ads.filter { item ->
            (item.email == null) || (item.email == request.impressionContext?.email)
        }
    }
}