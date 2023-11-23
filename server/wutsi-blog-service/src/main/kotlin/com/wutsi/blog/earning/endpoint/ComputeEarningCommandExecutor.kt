package com.wutsi.blog.earning.endpoint

import com.wutsi.blog.earning.service.WPPEarningService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/earnings/commands/compute")
class ComputeEarningCommandExecutor(
    private val service: WPPEarningService,
    @Value("\${wutsi.application.wpp.monhtly-budget}") private val monthlyBudget: Long
) {
    @GetMapping
    fun compute(
        @RequestParam year: Int,
        @RequestParam month: Int,
    ) {
        service.compile(year, month, monthlyBudget)
    }
}
