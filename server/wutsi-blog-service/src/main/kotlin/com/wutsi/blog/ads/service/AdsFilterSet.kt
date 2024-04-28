package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.user.domain.UserEntity

class AdsFilterSet(private val filters: List<AdsFilter>) : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        request.impressionContext ?: return ads

        var current = ads
        filters.forEach { filter ->
            current = filter.filter(request, current, user)
        }
        return current
    }
}