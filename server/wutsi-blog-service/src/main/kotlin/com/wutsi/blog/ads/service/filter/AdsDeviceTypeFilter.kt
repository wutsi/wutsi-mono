package com.wutsi.blog.ads.service.filter

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter

class AdsDeviceTypeFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>): List<AdsEntity> {
        request.impressionContext?.userAgent ?: return ads

        val ua = UAgentInfo(request.impressionContext!!.userAgent, "*/*")
        val mobile = ua.detectMobileQuick()
        val tablet = !mobile
        val desktop = !mobile
        return ads.filter { item ->
            (item.type.mobile == mobile) || (item.type.desktop == desktop) || (item.type.tablet == tablet)
        }
    }
}