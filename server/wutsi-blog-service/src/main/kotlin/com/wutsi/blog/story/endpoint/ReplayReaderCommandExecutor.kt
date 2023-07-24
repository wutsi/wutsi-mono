package com.wutsi.blog.kpi.endpoint

import com.wutsi.blog.kpi.service.KpiService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/kpis/commands/replay")
class ReplayKpiCommandExecutor(
    private val service: KpiService,
) {
    @GetMapping
    fun replay(
        @RequestParam year: Int,
        @RequestParam(required = false) month: Int? = null,
    ) {
        service.replay(year, month)
    }
}
