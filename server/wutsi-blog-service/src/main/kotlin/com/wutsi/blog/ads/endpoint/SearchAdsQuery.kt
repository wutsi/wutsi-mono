package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dto.AdsSummary
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.SearchAdsResponse
import com.wutsi.blog.ads.service.AdsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchAdsQuery(
    private val service: AdsService,
) {
    @PostMapping("/v1/ads/queries/search")
    fun execute(@Valid @RequestBody command: SearchAdsRequest): SearchAdsResponse {
        val ads = service.search(command)
        return SearchAdsResponse(
            ads = ads.map {
                AdsSummary(
                    id = it.id ?: "",
                    status = it.status,
                    title = it.title,
                    imageUrl = it.imageUrl,
                    creationDateTime = it.creationDateTime,
                    modificationDateTime = it.modificationDateTime,
                    totalClicks = it.totalClicks,
                    totalImpressions = it.totalImpressions,
                    endDate = it.endDate,
                    startDate = it.startDate,
                    currency = it.currency,
                    budget = it.budget,
                    dailyBudget = service.computeDailyBudget(it.type),
                    durationDays = service.computeDuration(it.startDate, it.endDate),
                    type = it.type,
                    url = it.url,
                    ctaType = it.ctaType,
                    transactionId = it.transaction?.id,
                )
            }
        )
    }
}
