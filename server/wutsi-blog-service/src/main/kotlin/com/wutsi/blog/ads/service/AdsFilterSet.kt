package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest

class AdsFilterSet(private val filters: List<AdsFilter>) : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>): List<AdsEntity> {
        var current = ads
        filters.forEach { filter ->
            current = filter.filter(request, current)
        }
        return current
    }
}