package com.wutsi.blog.ads.service.filter

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsDeviceTypeFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        request.impressionContext?.userAgent ?: return ads

        val ua = UAgentInfo(request.impressionContext?.userAgent, "*/*")
        val mobile = ua.detectMobileQuick()
        val tablet = ua.detectIpad() || ua.detectAndroidTablet()
        val desktop = !mobile && !tablet
        return ads.filter { item ->
            (mobile && item.type.mobile) || (desktop && item.type.desktop) || (tablet && item.type.tablet)
        }
    }
}