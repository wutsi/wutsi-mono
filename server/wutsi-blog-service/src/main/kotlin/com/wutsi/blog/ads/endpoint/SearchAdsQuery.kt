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
            ads = ads.map { ads ->
                AdsSummary(
                    id = ads.id ?: "",
                    status = ads.status,
                    title = ads.title,
                    imageUrl = ads.imageUrl,
                    creationDateTime = ads.creationDateTime,
                    modificationDateTime = ads.modificationDateTime,
                    totalClicks = ads.totalClicks,
                    totalImpressions = ads.totalImpressions,
                    endDate = ads.endDate,
                    startDate = ads.startDate,
                )
            }
        )
    }
}
