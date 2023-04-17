package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.widget.WidgetL10n.getText
import com.wutsi.checkout.manager.dto.Order
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.regulation.Country
import org.springframework.context.i18n.LocaleContextHolder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class DonationWidget(
    private val created: OffsetDateTime,
    private val customerName: String,
    private val totalPrice: Long,
    private val country: Country,
    private val action: Action? = null,
    private val showDate: Boolean,
) : CompositeWidgetAware() {
    companion object {
        fun of(
            order: OrderSummary,
            country: Country,
            action: Action? = null,
            showDate: Boolean = true,
            timezoneId: String?,
        ): DonationWidget =
            DonationWidget(
                created = timezoneId?.let { DateTimeUtil.convert(order.created, it) } ?: order.created,
                customerName = order.customerName,
                totalPrice = order.totalPrice,
                country = country,
                action = action,
                showDate = showDate,
            )

        fun of(
            order: Order,
            country: Country,
            action: Action? = null,
            timezoneId: String?,
            showDate: Boolean = true,
        ): DonationWidget =
            DonationWidget(
                created = timezoneId?.let { DateTimeUtil.convert(order.created, it) } ?: order.created,
                customerName = order.customerName,
                totalPrice = order.totalPrice,
                country = country,
                action = action,
                showDate = showDate,
            )
    }

    override fun toWidgetAware(): WidgetAware {
        val moneyFormat = country.createMoneyFormat()
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormatShort, LocaleContextHolder.getLocale())

        return Container(
            padding = 10.0,
            action = action,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    Row(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOfNotNull(
                            if (showDate) {
                                Text(
                                    caption = dateFormat.format(created),
                                    color = Theme.COLOR_GRAY,
                                )
                            } else {
                                null
                            },
                            if (showDate) {
                                Container(padding = 5.0)
                            } else {
                                null
                            },
                            Text(
                                caption = getText(
                                    key = "widget.donation.message",
                                    args = arrayOf(moneyFormat.format(totalPrice), customerName),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
