package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsCountryFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        user ?: return ads

        return ads.filter { item ->
            item.country.isNullOrEmpty() || item.country.equals(user.country, true)
        }
    }
}