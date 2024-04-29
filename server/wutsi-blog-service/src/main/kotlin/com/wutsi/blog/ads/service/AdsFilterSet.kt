package com.wutsi.blog.ads.service

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.logging.KVLogger

class AdsFilterSet(
    private val filters: List<AdsFilter>,
    private val logger: KVLogger,
) : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        request.impressionContext ?: return ads

        var count = 0
        logger.add("ads_filter_$count", ads.size)

        var current = ads
        filters.forEach { filter ->
            current = filter.filter(request, current, user)

            count++
            logger.add("ads_filter_${count}_" + filter.javaClass.simpleName, current.size)
        }
        return current
    }
}