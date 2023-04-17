package com.wutsi.application.checkout.order.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.util.StringUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.Order
import com.wutsi.checkout.manager.dto.OrderItem
import com.wutsi.checkout.manager.dto.UpdateOrderStatusRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.ProductType
import com.wutsi.enums.TransactionType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/orders/2")
class OrderV2Screen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractSecuredEndpoint() {
    companion object {
        const val PRODUCT_PICTURE_SIZE = 64.0
        const val PROVIDER_PICTURE_SIZE = 48.0
    }

    @PostMapping
    fun index(@RequestParam id: String): Widget {
        val order = checkoutManagerApi.getOrder(id).order
        val country = regulationEngine.country(order.business.country)
        val dateFormat = DateTimeFormatter.ofPattern(country.dateTimeFormat, LocaleContextHolder.getLocale())
        val moneyFormat = country.createMoneyFormat()
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member

        return Screen(
            id = Page.ORDER,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.order.app-bar.title", arrayOf(order.shortId)),
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(member),
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    children = listOfNotNull(
                        toCustomerWidget(order, member, dateFormat),
                        toToolbarWidget(order),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toOrderIdWidget(order),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toItemListWidget(order, moneyFormat),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toPriceWidget(order, moneyFormat, dateFormat),
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER),
                        toNotesWidget(order),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toToolbarWidget(order: Order): WidgetAware? {
        val buttons = mutableListOf<WidgetAware>()

        // Accept button
        if (order.status == OrderStatus.OPENED.name) {
            buttons.addAll(
                listOf(
                    Button(
                        padding = 5.0,
                        type = ButtonType.Elevated,
                        caption = getText("page.order.button.accept"),
                        stretched = false,
                        action = executeCommand(
                            urlBuilder.build("${Page.getOrderUrl()}/accept"),
                            parameters = mapOf("id" to order.id),
                        ),
                    ),
                    Button(
                        padding = 5.0,
                        type = ButtonType.Outlined,
                        caption = getText("page.order.button.reject"),
                        stretched = false,
                        action = executeCommand(
                            urlBuilder.build("${Page.getOrderUrl()}/cancel"),
                            parameters = mapOf("id" to order.id),
                        ),
                    ),
                ),
            )
        }

        // Complete Button
        if (order.status == OrderStatus.IN_PROGRESS.name) {
            buttons.addAll(
                listOf(
                    Button(
                        padding = 5.0,
                        type = ButtonType.Elevated,
                        caption = getText("page.order.button.complete"),
                        stretched = false,
                        action = executeCommand(
                            urlBuilder.build("${Page.getOrderUrl()}/complete"),
                            parameters = mapOf("id" to order.id),
                        ),
                    ),
                    Button(
                        padding = 5.0,
                        type = ButtonType.Outlined,
                        caption = getText("page.order.button.cancel"),
                        stretched = false,
                        action = executeCommand(
                            urlBuilder.build("${Page.getOrderUrl()}/cancel"),
                            parameters = mapOf("id" to order.id),
                        ),
                    ),
                ),
            )
        }

        return if (buttons.isEmpty()) {
            null
        } else {
            Container(
                padding = 10.0,
                child = Row(
                    mainAxisAlignment = MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    children = buttons,
                ),
            )
        }
    }

    @PostMapping("/accept")
    fun accept(@RequestParam id: String): Action =
        updateStatus(id, OrderStatus.IN_PROGRESS)

    @PostMapping("/cancel")
    fun cancel(@RequestParam id: String): Action =
        updateStatus(id, OrderStatus.CANCELLED)

    @PostMapping("/complete")
    fun complete(@RequestParam id: String): Action =
        updateStatus(id, OrderStatus.COMPLETED)

    private fun updateStatus(id: String, status: OrderStatus): Action {
        checkoutManagerApi.updateOrderStatus(
            request = UpdateOrderStatusRequest(
                orderId = id,
                status = status.name,
            ),
        )
        return gotoUrl(
            url = urlBuilder.build(Page.getOrderUrl()),
            parameters = mapOf("id" to id),
            replacement = true,
        )
    }

    fun toCustomerWidget(order: Order, member: Member, dateFormat: DateTimeFormatter) = Container(
        padding = 10.0,
        alignment = Alignment.Center,
        child = Column(
            mainAxisAlignment = MainAxisAlignment.center,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOf(
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = StringUtil.capitalize(order.customerName),
                        bold = true,
                        size = Theme.TEXT_SIZE_X_LARGE,
                    ),
                ),
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = StringUtil.capitalize(order.customerEmail.lowercase()),
                    ),
                ),
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = dateFormat.format(
                            DateTimeUtil.convert(order.created, member.timezoneId),
                        ),
                    ),
                ),
                Container(
                    padding = 5.0,
                    child = Text(
                        caption = getText("order.status.${order.status}"),
                        color = when (order.status) {
                            OrderStatus.OPENED.name -> Theme.COLOR_PRIMARY
                            OrderStatus.COMPLETED.name -> Theme.COLOR_SUCCESS
                            OrderStatus.CANCELLED.name, OrderStatus.EXPIRED.name -> Theme.COLOR_DANGER
                            OrderStatus.CANCELLED.name -> Theme.COLOR_SUCCESS
                            else -> null
                        },
                    ),
                ),
            ),
        ),
    )

    private fun toOrderIdWidget(order: Order) = Container(
        padding = 10.0,
        child = Row(
            children = listOf(
                Text(
                    caption = getText("page.order.order-id"),
                    bold = true,
                ),
                Text(order.id),
            ),
        ),
    )

    private fun toItemListWidget(order: Order, moneyFormat: DecimalFormat) = Container(
        padding = 10.0,
        child = Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = order.items.map { toItemWidget(it, moneyFormat) },
        ),
    )

    private fun toItemWidget(item: OrderItem, monetaryFormat: DecimalFormat) = Row(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.center,
        children = listOfNotNull(
            Container(
                padding = 5.0,
                margin = 5.0,
                background = Theme.COLOR_GRAY_LIGHT,
                borderRadius = 5.0,
                child = Text(
                    caption = item.quantity.toString(),
                    color = Theme.COLOR_BLACK,
                ),
            ),
            item.pictureUrl?.let {
                Container(
                    padding = 5.0,
                    child = ClipRRect(
                        borderRadius = 5.0,
                        child = Image(
                            width = PRODUCT_PICTURE_SIZE,
                            height = PRODUCT_PICTURE_SIZE,
                            fit = BoxFit.fill,
                            url = imageService.transform(
                                url = it,
                                Transformation(
                                    dimension = Dimension(
                                        width = PRODUCT_PICTURE_SIZE.toInt(),
                                        height = PRODUCT_PICTURE_SIZE.toInt(),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
            },
            Flexible(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Container(
                            padding = 5.0,
                            child = Text(
                                caption = if (item.productType == ProductType.DONATION.name) {
                                    getText("page.order.donation")
                                } else {
                                    item.title
                                },
                            ),
                        ),
                        Container(
                            padding = 5.0,
                            child = Text(
                                caption = getText("page.order.unit-price") +
                                    ": " +
                                    monetaryFormat.format(item.unitPrice),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )

    private fun toPriceWidget(order: Order, moneyFormat: DecimalFormat, dateFormat: DateTimeFormatter): WidgetAware {
        val tx = order.transactions
            .find { it.type == TransactionType.CHARGE.name && it.status == Status.SUCCESSFUL.name }

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOfNotNull(

                if (order.subTotalPrice != order.totalPrice) {
                    tableRow(getText("page.order.sub-total") + ":", moneyFormat.format(order.subTotalPrice))
                } else {
                    null
                },

                if (order.totalDiscount > 0) {
                    tableRow(
                        getText("page.order.discount"),
                        "-" + moneyFormat.format(order.totalDiscount),
                        color = Theme.COLOR_SUCCESS,
                    )
                } else {
                    null
                },

                tableRow(
                    getText("page.order.total"),
                    moneyFormat.format(order.totalPrice),
                    bold = true,
                    size = Theme.TEXT_SIZE_LARGE,
                    color = Theme.COLOR_PRIMARY,
                ),
                tx?.let {
                    Divider(height = 1.0, color = Theme.COLOR_DIVIDER)
                },

                tx?.let {
                    tableRow(
                        Row(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.center,
                            children = listOf(
                                Image(
                                    url = tx.paymentMethod.provider.logoUrl,
                                    fit = BoxFit.fill,
                                    width = PROVIDER_PICTURE_SIZE,
                                    height = PROVIDER_PICTURE_SIZE,
                                ),
                                Container(padding = 5.0),
                                Column(
                                    mainAxisAlignment = MainAxisAlignment.start,
                                    crossAxisAlignment = CrossAxisAlignment.start,
                                    children = listOf(
                                        Text(tx.paymentMethod.number),
                                        Text(
                                            caption = getText(
                                                "page.order.paid-on",
                                                arrayOf(dateFormat.format(tx.created)),
                                            ),
                                            size = Theme.TEXT_SIZE_SMALL,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        moneyFormat.format(tx.amount),
                    )
                },

                tx?.let {
                    Container(
                        padding = 10.0,
                        alignment = Alignment.CenterRight,
                        action = gotoUrl(
                            url = urlBuilder.build(Page.getTransactionUrl()),
                            parameters = mapOf(
                                "id" to it.id,
                                "hide-order" to "true",
                            ),
                        ),
                        child = Text(
                            caption = getText("page.order.payment-details"),
                            decoration = TextDecoration.Underline,
                            color = Theme.COLOR_PRIMARY,
                        ),
                    )
                },
            ),
        )
    }

    private fun toNotesWidget(order: Order): WidgetAware? =
        if (order.notes.isNullOrEmpty()) {
            null
        } else {
            Container(
                padding = 10.0,
                margin = 10.0,
                border = 1.0,
                borderColor = Theme.COLOR_PRIMARY,
                background = Theme.COLOR_PRIMARY_LIGHT,
                width = Double.MAX_VALUE,
                child = Text(order.notes!!),
            )
        }

    private fun tableRow(
        name: String,
        value: String,
        bold: Boolean? = null,
        color: String? = null,
        size: Double? = null,
    ) = tableRow(
        name = Text(name, size = size),
        value = value,
        bold = bold,
        color = color,
        size = size,
    )

    private fun tableRow(
        name: WidgetAware,
        value: String,
        bold: Boolean? = null,
        color: String? = null,
        size: Double? = null,
    ) = Row(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.center,
        children = listOf(
            Flexible(
                flex = 2,
                child = Container(
                    padding = 10.0,
                    child = name,
                ),
            ),
            Flexible(
                flex = 1,
                child = Container(
                    padding = 10.0,
                    child = Text(value, alignment = TextAlignment.Right, bold = bold, color = color, size = size),
                ),
            ),
        ),
    )
}
