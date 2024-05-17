package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsCategoryFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        return ads.filter { item ->
            item.category == null || item.category?.id == request.impressionContext?.categoryId ||
                    (item.category?.parent != null && item.category?.parent?.id == request.impressionContext?.categoryId)
        }
    }
}