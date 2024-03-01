package com.wutsi.blog.app.mapper

import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.AdsSummary
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.app.model.MoneyModel
import org.springframework.stereotype.Service

@Service
class AdsMapper {
    fun toAdsModel(ads: Ads) = AdsModel(
        id = ads.id,
        userId = ads.userId,
        creationDateTime = ads.creationDateTime,
        modificationDateTime = ads.modificationDateTime,
        completedDateTime = ads.completedDateTime,
        startDate = ads.startDate,
        endDate = ads.endDate,
        maxImpressions = ads.maxImpressions,
        budget = MoneyModel(ads.budget, ads.currency),
        maxDailyImpressions = ads.maxDailyImpressions,
        status = ads.status,
        url = ads.imageUrl,
        totalClicks = ads.totalClicks,
        imageUrl = ads.imageUrl,
        totalImpressions = ads.totalImpressions,
        title = ads.title,
        durationDays = ads.durationDays,
        type = ads.type,
        ctaType = ads.ctaType,
    )

    fun toAdsModel(ads: AdsSummary) = AdsModel(
        id = ads.id,
        creationDateTime = ads.creationDateTime,
        modificationDateTime = ads.modificationDateTime,
        startDate = ads.startDate,
        endDate = ads.endDate,
        status = ads.status,
        url = ads.imageUrl,
        totalClicks = ads.totalClicks,
        imageUrl = ads.imageUrl,
        totalImpressions = ads.totalImpressions,
        title = ads.title,
    )
}
