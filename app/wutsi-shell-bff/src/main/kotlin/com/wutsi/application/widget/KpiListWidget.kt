package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.DonationKpiSummary
import com.wutsi.checkout.manager.dto.SalesKpiSummary
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.regulation.Country

class KpiListWidget(
    private val kpis: List<KpiWidget>,
) : CompositeWidgetAware() {
    companion object {
        fun of(business: Business, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = business.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = business.totalSales,
                        country = country,
                        money = true,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.views"),
                        value = business.totalViews,
                        country = country,
                        money = false,
                    ),
                ),
            )

        fun of(product: Product, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = product.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = product.totalSales,
                        country = country,
                        money = true,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.views"),
                        value = product.totalViews,
                        country = country,
                        money = false,
                    ),
                ),
            )

        fun of(kpi: SalesKpiSummary, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.orders"),
                        value = kpi.totalOrders,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.sales"),
                        value = kpi.totalValue,
                        country = country,
                        money = true,
                    ),
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.views"),
                        value = kpi.totalViews,
                        country = country,
                        money = false,
                    ),
                ),
            )

        fun of(kpi: DonationKpiSummary, country: Country): KpiListWidget =
            KpiListWidget(
                kpis = listOf(
                    KpiWidget(
                        name = WidgetL10n.getText("widget.kpi.donations"),
                        value = kpi.totalDonations,
                        country = country,
                        money = false,
                    ),
                    KpiWidget(
                        name = "",
                        value = kpi.totalValue,
                        country = country,
                        money = true,
                    ),
                ),
            )
    }

    override fun toWidgetAware(): WidgetAware {
        val children = kpis.flatMap {
            listOf(
                it,
                Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
            )
        }.toMutableList()
        children.removeLast()

        return Container(
            background = Theme.COLOR_WHITE,
            borderRadius = 5.0,
            child = Column(
                children = children,
            ),
        )
    }
}
