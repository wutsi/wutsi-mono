package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dao.UserKpiRepository
import com.wutsi.blog.kpi.domain.UserKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Service
class TransactionRateKpiImporter(
    private val dao: UserKpiRepository,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long {
        val total = dao.findByUserIdAndTypeAndYearAndMonthAndSource(
            userId = 0L,
            type = KpiType.TRANSACTION,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()

        val success = dao.findByUserIdAndTypeAndYearAndMonthAndSource(
            userId = 0L,
            type = KpiType.TRANSACTION_SUCCESS,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()

        val value = total?.let {
            10000 * (success?.value ?: 0L) / total.value
        } ?: 0

        val rate = dao.findByUserIdAndTypeAndYearAndMonthAndSource(
            userId = 0L,
            type = KpiType.TRANSACTION_RATE,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()
        if (value == 0L) {
            if (rate != null) {
                dao.delete(rate)
            }
        } else {
            if (rate != null) {
                rate.value = value
                dao.save(rate)
            } else {
                dao.save(
                    UserKpiEntity(
                        userId = 0L,
                        type = KpiType.TRANSACTION_RATE,
                        year = date.year,
                        month = date.monthValue,
                        source = TrafficSource.ALL,
                        value = value,
                    )
                )
            }
        }

        return 1L
    }
}
