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
                durationDays = ads.durationDays,
                imageUrl = ads.imageUrl,
                creationDateTime = ads.creationDateTime,
                modificationDateTime = ads.modificationDateTime,
                totalClicks = ads.totalClicks,
                totalImpressions = ads.totalImpressions,
                url = ads.url,
                endDate = ads.endDate,
                startDate = ads.startDate,
                completedDateTime = ads.completedDateTime,
            )
        )
    }
}
