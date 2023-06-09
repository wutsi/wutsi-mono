package com.wutsi.tracking.manager.endpoint

import com.wutsi.tracking.manager.delegate.ReplayKpiDelegate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ReplayKpiController(
    val `delegate`: ReplayKpiDelegate,
) {
    @GetMapping("/v1/kpis/replay")
    public fun invoke(
        @RequestParam year: Int,
        @RequestParam(required = false) month: Int? = null,
        @RequestParam(required = false) day: Int? = null,
    ) {
        delegate.invoke(year, month)
    }
}
