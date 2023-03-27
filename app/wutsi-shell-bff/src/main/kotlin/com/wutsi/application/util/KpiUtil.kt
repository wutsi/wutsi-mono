package com.wutsi.application.util

import com.wutsi.checkout.manager.dto.SalesKpiSummary
import com.wutsi.flutter.sdui.ChartData
import java.time.LocalDate

object KpiUtil {
    fun toChartDataList(
        kpis: List<SalesKpiSummary>,
        from: LocalDate,
        to: LocalDate,
        type: ChartDataType,
    ): List<ChartData> {
        val kpiMap = kpis.associateBy { it.date }
        val data = mutableListOf<ChartData>()

        var cur = from
        while (!cur.isAfter(to)) {
            data.add(
                ChartData(
                    x = cur.toString(),
                    y = when (type) {
                        ChartDataType.ORDERS -> kpiMap[cur]?.totalOrders?.toDouble() ?: 0.0
                        ChartDataType.VIEWS -> kpiMap[cur]?.totalViews?.toDouble() ?: 0.0
                        ChartDataType.SALES -> kpiMap[cur]?.totalValue?.toDouble() ?: 0.0
                    },
                ),
            )
            cur = cur.plusDays(1)
        }
        return data
    }
}

enum class ChartDataType {
    ORDERS,
    VIEWS,
    SALES,
}
