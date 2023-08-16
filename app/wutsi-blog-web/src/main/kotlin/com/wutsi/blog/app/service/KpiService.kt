package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.KpiBackend
import com.wutsi.blog.app.mapper.KpiMapper
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.BarChartSerieModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class KpiService(
    private val backend: KpiBackend,
    private val mapper: KpiMapper,
) {
    fun search(request: SearchStoryKpiRequest): List<KpiModel> =
        backend.search(request).kpis.map { mapper.toKpiModel(it) }

    fun search(request: SearchUserKpiRequest): List<KpiModel> =
        backend.search(request).kpis.map { mapper.toKpiModel(it) }

    fun toKpiModel(kpis: List<KpiModel>, type: KpiType): BarChartModel {
        val kpiByDate = kpis.associateBy { it.date }
        val categoryByDate = toBarCharCategories(kpiByDate.keys.toList())
        val fmt = DateTimeFormatter.ofPattern("MMM yyyy", LocaleContextHolder.getLocale())
        return BarChartModel(
            categories = categoryByDate.map { it.format(fmt) },
            series = listOf(
                BarChartSerieModel(
                    name = type.name,
                    data = categoryByDate.map {
                        (kpiByDate[it]?.value ?: 0).toDouble()
                    },
                ),
            ),
        )
    }

    fun toKpiModelBySource(kpis: List<KpiModel>, type: KpiType): BarChartModel {
        val sources = kpis.sortedBy { it.value }.map { it.source }.toSet()
        val total = kpis.sumOf { it.value }

        return BarChartModel(
            categories = sources.map { it.name },
            series = listOf(
                BarChartSerieModel(
                    name = type.name,
                    data = sources.map { source ->
                        100.0 * kpis.filter { it.source == source }.sumOf { it.value.toDouble() } / total
                    },
                ),
            ),
        )
    }

    private fun toBarCharCategories(dates: List<LocalDate>): List<LocalDate> {
        if (dates.isEmpty()) {
            return emptyList()
        } else if (dates.size == 1) {
            return dates
        } else {
            val sorted = dates.sorted()
            val first = sorted.first()
            val last = sorted.last()

            var cur = first
            val series = mutableListOf<LocalDate>()
            while (cur.isBefore(last) || cur.isEqual(last)) {
                series.add(cur)
                cur = cur.plusMonths(1)
            }
            return series
        }
    }
}
