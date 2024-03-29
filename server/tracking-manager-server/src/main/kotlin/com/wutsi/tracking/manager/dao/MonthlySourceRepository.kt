package com.wutsi.tracking.manager.dao

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MonthlySourceRepository : AbstractSourceRepository() {
    override fun getStorageFolder(date: LocalDate): String =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM"))
}
