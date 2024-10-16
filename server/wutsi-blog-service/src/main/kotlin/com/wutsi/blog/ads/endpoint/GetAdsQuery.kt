package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.ads.service.AdsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetAdsQuery(
    private val service: AdsService,
) {
    @GetMapping("/v1/ads/{id}")
    fun execute(@PathVariable id: String): GetAdsResponse {
        val ads = service.findById(id)
        return GetAdsResponse(
            ads = Ads(
                id = ads.id ?: "",
                userId = ads.userId,
                type = ads.type,
                status = ads.status,
                ctaType = ads.ctaType,
                title = ads.title,
                imageUrl = ads.imageUrl,
                startDate = ads.startDate,
                endDate = ads.endDate,
                creationDateTime = ads.creationDateTime,
                modificationDateTime = ads.modificationDateTime,
                publishedDateTime = ads.publishedDateTime,
                completedDateTime = ads.completedDateTime,
                totalClicks = ads.totalClicks,
                totalImpressions = ads.totalImpressions,
                url = ads.url,
                currency = ads.currency,
                budget = ads.budget,
                dailyBudget = service.computeDailyBudget(ads.type),
                durationDays = service.computeDuration(ads.startDate, ads.endDate),
                maxDailyImpressions = ads.maxDailyImpressions,
                maxImpressions = ads.maxImpressions,
                country = ads.country,
                language = ads.language,
                gender = ads.gender,
                os = ads.os,
                email = ads.email,
                transactionId = ads.transaction?.id,
                categoryId = ads.category?.id,
                orderCount = ads.orderCount,
                totalSales = ads.totalSales,
            )
        )
    }
}
