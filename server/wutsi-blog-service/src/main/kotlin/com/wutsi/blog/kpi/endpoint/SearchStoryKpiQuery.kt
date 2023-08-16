package com.wutsi.blog.kpi.endpoint

import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.StoryKpi
import com.wutsi.blog.kpi.service.KpiService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchStoryKpiQuery(
    private val service: KpiService,
) {
    @PostMapping("/v1/kpis/queries/search-story")
    fun search(@Valid @RequestBody request: SearchStoryKpiRequest): SearchStoryKpiResponse =
        SearchStoryKpiResponse(
            kpis = service.search(request).map {
                StoryKpi(
                    id = it.id,
                    storyId = it.storyId,
                    year = it.year,
                    month = it.month,
                    type = it.type,
                    value = it.value,
                    source = it.source,
                )
            },
        )
}
