package com.wutsi.blog.ads.service.filter

import au.com.flyingkite.mobiledetect.UAgentInfo
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.OS
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.domain.UserEntity

class AdsOSFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        request.impressionContext?.userAgent ?: return ads

        val ua = UAgentInfo(request.impressionContext?.userAgent, "*/*")
        return ads.filter { item ->
            item.os == null ||
                    (item.os == OS.ANDROID && ua.detectAndroid()) ||
                    (item.os == OS.IOS && ua.detectIos()) ||
                    (item.os == OS.WINDOWS && (ua.detectWindowsMobile() || ua.detectWindowsPhone()))
        }
    }
}