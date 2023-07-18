package com.wutsi.blog.kpi.endpoint

import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import com.wutsi.blog.kpi.dto.UserKpi
import com.wutsi.blog.kpi.service.KpiService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchUserKpiQuery(
    private val service: KpiService,
) {
    @PostMapping("/v1/kpis/queries/search-user")
    fun search(@Valid @RequestBody request: SearchUserKpiRequest): SearchUserKpiResponse =
        SearchUserKpiResponse(
            kpis = service.search(request).map {
                UserKpi(
                    id = it.id,
                    userId = it.userId,
                    year = it.year,
                    month = it.month,
                    type = it.type,
                    value = it.value,
                )
            },
        )
}
