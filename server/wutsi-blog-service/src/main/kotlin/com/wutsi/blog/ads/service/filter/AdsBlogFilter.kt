package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsBlogFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        val result = ads.filter { ad -> ad.userId == request.impressionContext?.blogId }
        return if (result.isEmpty()) {
            ads
        } else {
            result
        }
    }
}
