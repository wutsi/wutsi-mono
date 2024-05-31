package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.KpiBackend
import com.wutsi.blog.app.mapper.KpiMapper
import com.wutsi.blog.app.model.BarChartModel
import com.wutsi.blog.app.model.BarChartSerieModel
import com.wutsi.blog.app.model.KpiModel
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.TrafficSource
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class KpiService(
    private val backend: KpiBackend,
    private val mapper: KpiMapper,
    private val messages: MessageSource,
) {
    fun search(request: SearchStoryKpiRequest): List<KpiModel> =
        backend.search(request).kpis.map { mapper.toKpiModel(it) }

    fun search(request: SearchUserKpiRequest): List<KpiModel> =
        backend.search(request).kpis.map { mapper.toKpiModel(it) }

    fun toBarChartModel(kpis: List<KpiModel>): BarChartModel {
        val kpiByDate = kpis.groupBy { it.date }
        val categoryByDate = toBarCharCategories(kpiByDate.keys.toList())
        val fmt = DateTimeFormatter.ofPattern("MMM yyyy", LocaleContextHolder.getLocale())
        val kpiTypes = kpis.map { it.type }.toSet().toList()
        return BarChartModel(
            categories = categoryByDate.map { it.format(fmt) },
            series = kpiTypes.map { type ->
                BarChartSerieModel(
                    name = toLabel(type),
                    data = categoryByDate.map { date ->
                        (kpiByDate[date]?.filter { it.type == type }
                            ?.sumOf { it.value } ?: 0)
                            .toDouble()
                    },
                )
            },
        )
    }

    private fun toLabel(type: KpiType): String =
        when (type) {
            KpiType.DONATION -> getText("label.donations")
            KpiType.DONATION_VALUE -> getText("label.donations")
            KpiType.DURATION -> getText("label.read_time") + " - " + getText("label.hours")
            KpiType.READ -> getText("label.views")
            KpiType.READER -> getText("label.readers")
            KpiType.SALES -> getText("label.sales")
            KpiType.STORE -> getText("label.stores")
            KpiType.SUBSCRIPTION -> getText("label.subscribers")
            KpiType.USER -> getText("label.users")
            KpiType.USER_BLOG -> getText("label.blogs")
            KpiType.USER_WPP -> getText("label.partners")
            KpiType.WPP_EARNING -> getText("label.earnings")
            KpiType.WPP_BONUS -> getText("label.bonus")
            KpiType.TRANSACTION -> getText("label.transaction")
            KpiType.TRANSACTION_SUCCESS -> getText("label.success")
            KpiType.TRANSACTION_RATE -> getText("label.success_rate")
            else -> ""
        }

    fun toBarChartModelByTrafficSource(kpis: List<KpiModel>, type: KpiType): BarChartModel {
        val kpiByDate = kpis.groupBy { it.date }
        val categoryByDate = toBarCharCategories(kpiByDate.keys.toList())
        val fmt = DateTimeFormatter.ofPattern("MMM yyyy", LocaleContextHolder.getLocale())
        val series = TrafficSource.values()
            .filter { source -> source != TrafficSource.ALL && kpis.find { it.source == source } != null }
            .map { source ->
                BarChartSerieModel(
                    name = getText("traffic-source.$source"),
                    data = categoryByDate.map {
                        (kpiByDate[it]?.filter { it.source == source }?.sumOf { it.value } ?: 0)
                            .toDouble()
                    },
                )
            }
        return BarChartModel(
            categories = categoryByDate.map { it.format(fmt) },
            series = series,
        )
    }

    fun toKpiModelBySource(kpis: List<KpiModel>, type: KpiType): BarChartModel {
        val sources = kpis.sortedBy { it.value }.map { it.source }.toSet()

        val total = kpis.sumOf { it.value }
        val data = sources.map { source ->
            Pair(
                first = source,
                second = (100.0 * kpis.filter { it.source == source }.sumOf { it.value.toDouble() } / total),
            )
        }.sortedByDescending { it.second }

        val fmt = DecimalFormat("0.0")
        return BarChartModel(
            categories = data.map { getText("traffic-source.${it.first.name}") },
            series = listOf(
                BarChartSerieModel(
                    name = getText("label.traffic_percent"),
                    data = data.map { fmt.format(it.second).toDouble() },
                ),
            ),
        )
    }

    private fun getText(key: String): String =
        try {
            messages.getMessage(key, emptyArray(), LocaleContextHolder.getLocale())
        } catch (ex: Exception) {
            key
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
