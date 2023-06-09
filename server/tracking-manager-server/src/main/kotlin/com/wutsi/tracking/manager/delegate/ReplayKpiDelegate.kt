package com.wutsi.tracking.manager.delegate

import com.wutsi.tracking.manager.service.KpiService
import org.springframework.stereotype.Service

@Service
class ReplayKpiDelegate(private val service: KpiService) {
    fun invoke(year: Int, month: Int?) {
        service.replay(year, month)
    }
}
