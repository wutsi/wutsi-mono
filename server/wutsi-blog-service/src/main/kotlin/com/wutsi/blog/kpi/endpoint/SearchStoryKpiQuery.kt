package com.wutsi.blog.kpi.endpoint

import com.wutsi.blog.kpi.dto.Kpi
import com.wutsi.blog.kpi.dto.SearchKpiRequest
import com.wutsi.blog.kpi.dto.SearchKpiResponse
import com.wutsi.blog.kpi.service.KpiService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchKpiQuery(
    private val service: KpiService,
) {
    @PostMapping("/v1/kpis/queries/search")
    fun search(@Valid @RequestBody request: SearchKpiRequest): SearchKpiResponse =
        SearchKpiResponse(
            kpis = service.search(request).map {
                Kpi(
                    id = it.id,
                    storyId = it.storyId,
                    year = it.year,
                    month = it.month,
                    type = it.type,
                    value = it.value
                )
            }
        )
}
