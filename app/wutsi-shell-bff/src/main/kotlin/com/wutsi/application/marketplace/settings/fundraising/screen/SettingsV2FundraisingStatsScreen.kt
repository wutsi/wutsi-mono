package com.wutsi.application.marketplace.settings.fundraising.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.KpiUtil
import com.wutsi.application.widget.KpiListWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.SearchDonationKpiRequest
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Chart
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

@RestController
@RequestMapping("/settings/2/fundraising/stats")
class SettingsV2FundraisingStatsScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val clock: Clock,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        if (member.businessId == null) {
            return Screen(
                id = Page.SETTINGS_FUNDRAISING_STATS,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_WHITE,
                    foregroundColor = Theme.COLOR_BLACK,
                    title = getText("page.settings.fundraising.stats.app-bar.title"),
                ),
            ).toWidget()
        }

        val today = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC)
        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.settings.fundraising.stats.tab.28d").uppercase(), bold = true),
                Text(getText("page.settings.fundraising.stats.tab.overall").uppercase(), bold = true),
            ),
        )
        val tabViews = TabBarView(
            children = listOfNotNull(
                toKpiTab(member, business, today.minusDays(28), today),
                toKpiTab(member, business, business.created.toLocalDate(), today),
            ),
        )

        return DefaultTabController(
            id = Page.SETTINGS_FUNDRAISING_STATS,
            length = tabs.tabs.size,
            child = Screen(
                backgroundColor = Theme.COLOR_WHITE,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_PRIMARY,
                    foregroundColor = Theme.COLOR_WHITE,
                    title = getText("page.settings.fundraising.stats.app-bar.title"),
                    bottom = tabs,
                ),
                child = tabViews,
            ),
        ).toWidget()
    }

    private fun toKpiTab(
        member: Member,
        business: Business,
        from: LocalDate,
        to: LocalDate,
    ): WidgetAware =
        SingleChildScrollView(
            child = Column(
                children = listOfNotNull(
                    toKpiWidget(member, business, from, to),
                    toChartWidget(member, from, to),
                ),
            ),
        )

    private fun toKpiWidget(
        member: Member,
        business: Business,
        from: LocalDate,
        to: LocalDate,
    ): WidgetAware {
        val kpis = checkoutManagerApi.searchDonationKpi(
            request = SearchDonationKpiRequest(
                aggregate = true,
                businessId = member.businessId,
                fromDate = from,
                toDate = to,
            ),
        ).kpis
        if (kpis.isEmpty()) {
            return Container()
        }

        val country = regulationEngine.country(business.country)
        return Container(
            margin = 10.0,
            border = 1.0,
            borderColor = Theme.COLOR_DIVIDER,
            child = KpiListWidget.of(kpis[0], country),
        )
    }

    private fun toChartWidget(
        member: Member,
        from: LocalDate,
        to: LocalDate,
    ): WidgetAware {
        val request = SearchDonationKpiRequest(
            businessId = member.businessId,
            fromDate = from,
            toDate = to,
        )
        val kpis = checkoutManagerApi.searchDonationKpi(request = request).kpis

        return Column(
            children = listOf(
                Container(
                    border = 1.0,
                    margin = 10.0,
                    borderColor = Theme.COLOR_DIVIDER,
                    child = Chart(
                        title = getText("page.settings.fundraising.stats.donations"),
                        series = listOf(
                            KpiUtil.toDonationChartDataList(kpis, from, to),
                        ),
                    ),
                ),
            ),
        )
    }
}
