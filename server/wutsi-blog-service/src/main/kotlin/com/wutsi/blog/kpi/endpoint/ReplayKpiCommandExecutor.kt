package com.wutsi.blog.kpi.endpoint

import com.wutsi.blog.kpi.service.KpiService
import org.checkerframework.common.reflection.qual.GetMethod
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/kpis/commands/replay")
class ReplayKpi2CommandExecutor(
    private val service: KpiService,
) {
    @GetMethod
    fun replay(
        @RequestParam year: Int,
        @RequestParam(required = false) month: Int? = null,
    ) {
        service.replay(year, month)
    }

}
