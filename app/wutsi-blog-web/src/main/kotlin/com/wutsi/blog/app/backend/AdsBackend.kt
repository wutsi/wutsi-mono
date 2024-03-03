package com.wutsi.blog.app.backend

import com.wutsi.blog.ads.dto.CreateAdsCommand
import com.wutsi.blog.ads.dto.CreateAdsResponse
import com.wutsi.blog.ads.dto.GetAdsResponse
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.dto.SearchAdsResponse
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AdsBackend(
    private val rest: RestTemplate,
    @Value("\${wutsi.application.backend.ads.endpoint}") private val endpoint: String,
) {
    fun get(id: String): GetAdsResponse =
        rest.getForEntity("$endpoint/$id", GetAdsResponse::class.java).body!!

    fun create(request: CreateAdsCommand): CreateAdsResponse =
        rest.postForEntity("$endpoint/commands/create", request, CreateAdsResponse::class.java).body!!

    fun search(request: SearchAdsRequest): SearchAdsResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchAdsResponse::class.java).body!!

    fun updateAttribute(request: UpdateAdsAttributeCommand) {
        rest.postForEntity("$endpoint/commands/update-attribute", request, Any::class.java)
    }

    fun publish(request: PublishAdsCommand) {
        rest.postForEntity("$endpoint/commands/publish", request, Any::class.java)
    }
}
