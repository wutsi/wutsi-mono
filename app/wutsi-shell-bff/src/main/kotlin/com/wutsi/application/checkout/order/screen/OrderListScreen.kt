package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.OrderWidget
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
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.image.ImageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders/2/list")
class OrderListScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractSecuredEndpoint() {
    companion object {
        const val MAX_ORDERS = 100
    }

    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (!member.business || member.storeId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val orders = checkoutManagerApi.searchOrder(
            request = SearchOrderRequest(
                limit = MAX_ORDERS,
                businessId = business.id,
                type = OrderType.SALES.name,
                status = listOf(
                    OrderStatus.OPENED,
                    OrderStatus.IN_PROGRESS,
                    OrderStatus.COMPLETED,
                    OrderStatus.CANCELLED,
                ).map { it.name },
            ),
        ).orders

        return Screen(
            id = Page.PROFILE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.order.list.app-bar.title"),
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
                                getText("page.order.list.count-0")
                            } else if (orders.size == 1) {
                                getText("page.order.list.count-1")
                            } else {
                                getText("page.order.list.count-n", arrayOf(orders.size))
                            },
                        ),
                    ),
                    Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = toOrderListViewWidget(orders, business, member),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/fragment")
    fun fragment(@RequestParam(required = false) status: Array<OrderStatus> = emptyArray()): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (!member.business || member.businessId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        return toContentWidget(status, business, member).toWidget()
    }

    private fun toContentWidget(status: Array<OrderStatus>, business: Business, member: Member): WidgetAware {
        val orders = checkoutManagerApi.searchOrder(
            request = SearchOrderRequest(
                limit = MAX_ORDERS,
                businessId = business.id,
                status = status.map { it.name },
            ),
        ).orders
        return Column(
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOfNotNull(
                Container(
                    padding = 10.0,
                    child = Text(
                        caption = if (orders.isEmpty()) {
                            getText("page.order.list.count-0")
                        } else if (orders.size == 1) {
                            getText("page.order.list.count-1")
                        } else {
                            getText("page.order.list.count-n", arrayOf(orders.size))
                        },
                    ),
                ),
                Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                Flexible(
                    child = ListView(
                        separator = true,
                        separatorColor = Theme.COLOR_DIVIDER,
                        children = orders.map {
                            toOrderListItemWidget(it, business, member)
                        },
                    ),
                ),
            ),
        )
    }

    private fun toOrderListViewWidget(orders: List<OrderSummary>, business: Business, member: Member): WidgetAware {
        val children = mutableListOf<WidgetAware>()

        addListItems(
            caption = getText("page.order.list.in-progress"),
            orders = orders.filter { it.status == OrderStatus.OPENED.name || it.status == OrderStatus.IN_PROGRESS.name },
            business = business,
            member = member,
            children = children,
        )
        addListItems(
            caption = getText("page.order.list.closed"),
            orders = orders.filter { it.status != OrderStatus.OPENED.name && it.status != OrderStatus.IN_PROGRESS.name },
            business = business,
            member = member,
            children = children,
        )
        return ListView(
            separator = true,
            separatorColor = Theme.COLOR_DIVIDER,
            children = children,
        )
    }

    private fun addListItems(
        caption: String,
        orders: List<OrderSummary>,
        business: Business,
        member: Member,
        children: MutableList<WidgetAware>,
    ) {
        if (orders.isNotEmpty()) {
            children.add(
                Container(
                    alignment = Alignment.CenterLeft,
                    background = Theme.COLOR_GRAY_LIGHT,
                    padding = 10.0,
                    width = Double.MAX_VALUE,
                    child = Text(
                        caption = caption,
                        bold = true,
                        size = Theme.TEXT_SIZE_LARGE,
                    ),
                ),
            )
            children.addAll(
                orders.map { toOrderListItemWidget(it, business, member) },
            )
        }
    }

    private fun toOrderListItemWidget(order: OrderSummary, business: Business, member: Member): WidgetAware =
        OrderWidget.of(
            order = order,
            country = regulationEngine.country(business.country),
            imageService = imageService,
            action = gotoUrl(
                url = urlBuilder.build(Page.getOrderUrl()),
                parameters = mapOf(
                    "id" to order.id,
                ),
            ),
            timezoneId = member.timezoneId,
        )
}
