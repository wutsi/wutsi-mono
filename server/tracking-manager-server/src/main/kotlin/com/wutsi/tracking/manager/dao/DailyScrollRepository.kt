package com.wutsi.tracking.manager.dao

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class DailyScrollRepository : AbstractScrollRepository() {
    override fun getStorageFolder(date: LocalDate): String =
        "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
