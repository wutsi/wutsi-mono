package com.wutsi.application.checkout.transaction.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.OrderWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.enums.TransactionType
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/transactions/2")
class Transaction2Screen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(
        @RequestParam id: String,
        @RequestParam(name = "hide-order", required = false) hideOrder: Boolean? = null,
    ): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val country = regulationEngine.country(member.country)
        val moneyFormat = country.createMoneyFormat()
        val dateFormat = DateTimeFormatter.ofPattern(country.dateTimeFormat, LocaleContextHolder.getLocale())
        val tx = checkoutManagerApi.getTransaction(id, false).transaction
        val order = if (tx.orderId != null && hideOrder != true) {
            checkoutManagerApi.getOrder(tx.orderId!!).order
        } else {
            null
        }

        return Screen(
            id = Page.TRANSACTION,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.transaction.app-bar.title", arrayOf(tx.id.uppercase().takeLast(4))),
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(member),
            child = SingleChildScrollView(
                child = Column(
                    children = listOfNotNull(
                        toRowWidget("page.transaction.id", Text(tx.id, size = Theme.TEXT_SIZE_SMALL)),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget(
                            "page.transaction.date",
                            DateTimeUtil.convert(tx.created, member.timezoneId).format(dateFormat),
                        ),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget("page.transaction.type", getText("transaction.type.${tx.type}")),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget(
                            key = if (tx.type == TransactionType.CHARGE.name) {
                                "page.transaction.from"
                            } else {
                                "page.transaction.to"
                            },
                            value = ListItem(
                                caption = tx.paymentMethod.number,
                                subCaption = tx.paymentMethod.ownerName,
                                leading = Image(
                                    width = 48.0,
                                    height = 48.0,
                                    url = tx.paymentMethod.provider.logoUrl,
                                ),
                            ),
                        ),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget(
                            "page.transaction.status",
                            getText("transaction.status.${tx.status}"),
                            bold = true,
                            color = when (tx.status.uppercase()) {
                                Status.FAILED.name -> Theme.COLOR_DANGER
                                Status.PENDING.name -> Theme.COLOR_WARNING
                                Status.SUCCESSFUL.name -> Theme.COLOR_SUCCESS
                                else -> null
                            },
                        ),
                        Divider(color = Theme.COLOR_DIVIDER),

                        if (tx.status == Status.FAILED.name) {
                            Column(
                                children = listOf(
                                    toRowWidget(
                                        "page.transaction.error",
                                        Text(
                                            caption = tx.errorCode ?: "",
                                            size = Theme.TEXT_SIZE_SMALL,
                                            maxLines = 5,
                                        ),
                                    ),
                                    Divider(color = Theme.COLOR_DIVIDER),
                                ),
                            )
                        } else {
                            null
                        },

                        toRowWidget("page.transaction.amount", moneyFormat.format(tx.amount)),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget("page.transaction.fees", moneyFormat.format(tx.fees)),
                        Divider(color = Theme.COLOR_DIVIDER),

                        toRowWidget("page.transaction.net", moneyFormat.format(tx.net)),
                        Divider(color = Theme.COLOR_DIVIDER),

                        order?.let {
                            toRowWidget(
                                "page.transaction.order",
                                OrderWidget.of(
                                    order = it,
                                    country = country,
                                    imageService = imageService,
                                    action = gotoUrl(
                                        url = urlBuilder.build(Page.getOrderUrl()),
                                        parameters = mapOf("id" to it.id),
                                    ),
                                    timezoneId = member.timezoneId,
                                    showDate = false,
                                ),
                            )
                        },
                        order?.let {
                            Divider(color = Theme.COLOR_DIVIDER)
                        },
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toRowWidget(key: String, value: String?, color: String? = null, bold: Boolean? = null): WidgetAware =
        toRowWidget(
            key,
            Container(
                padding = 10.0,
                child = Text(
                    value ?: "",
                    alignment = TextAlignment.Left,
                    color = color,
                    bold = bold,
                ),
            ),
        )

    private fun toRowWidget(key: String, value: WidgetAware): WidgetAware =
        Row(
            children = listOf(
                Flexible(
                    flex = 3,
                    child = Container(
                        padding = 10.0,
                        child = Text(
                            getText(key),
                            bold = true,
                            alignment = TextAlignment.Right,
                        ),
                    ),
                ),
                Flexible(
                    flex = 10,
                    child = value,
                ),
            ),
        )
}
