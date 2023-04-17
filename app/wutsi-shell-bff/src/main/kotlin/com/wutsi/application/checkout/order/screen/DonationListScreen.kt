package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.DonationWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.SearchOrderRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.OrderType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/donations/2/list")
class DonationListScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    companion object {
        const val MAX_ORDERS = 100
    }

    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (!member.business || member.fundraisingId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val orders = checkoutManagerApi.searchOrder(
            request = SearchOrderRequest(
                limit = MAX_ORDERS,
                businessId = business.id,
                type = OrderType.DONATION.name,
                status = listOf(
                    OrderStatus.COMPLETED,
                ).map { it.name },
            ),
        ).orders

        return Screen(
            id = Page.PROFILE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.donation.list.app-bar.title"),
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_SETTINGS,
                        action = Action(
                            type = ActionType.Route,
                            url = urlBuilder.build(Page.getSettingsUrl()),
                        ),
                    ),
                ),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = if (orders.isEmpty()) {
                                getText("page.donation.list.count-0")
                            } else if (orders.size == 1) {
                                getText("page.donation.list.count-1")
                            } else {
                                getText("page.donation.list.count-n", arrayOf(orders.size))
                            },
                        ),
                    ),
                    Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = toListViewWidget(orders, business, member),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toListViewWidget(orders: List<OrderSummary>, business: Business, member: Member): WidgetAware =
        ListView(
            separator = true,
            separatorColor = Theme.COLOR_DIVIDER,
            children = orders.map {
                DonationWidget.of(
                    order = it,
                    country = regulationEngine.country(business.country),
                    action = gotoUrl(
                        url = urlBuilder.build(Page.getOrderUrl()),
                        parameters = mapOf(
                            "id" to it.id,
                        ),
                    ),
                    timezoneId = member.timezoneId,
                )
            },
        )
}
