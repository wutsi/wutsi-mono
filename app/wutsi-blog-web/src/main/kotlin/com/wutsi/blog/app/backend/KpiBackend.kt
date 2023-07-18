package com.wutsi.blog.app.backend

import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class KpiBackend(
    private val rest: RestTemplate,
) {
    @Value("\${wutsi.application.backend.kpi.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchStoryKpiRequest): SearchStoryKpiResponse =
        rest.postForEntity("$endpoint/queries/search-story", request, SearchStoryKpiResponse::class.java).body!!

    fun search(request: SearchUserKpiRequest): SearchUserKpiResponse =
        rest.postForEntity("$endpoint/queries/search-user", request, SearchUserKpiResponse::class.java).body!!
}
