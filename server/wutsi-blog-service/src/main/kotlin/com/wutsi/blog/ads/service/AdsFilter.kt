package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest

interface AdsFilter {
    fun filter(request: SearchAdsRequest, ads: List<AdsEntity>): List<AdsEntity>
}