package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.widget.WidgetL10n.getText
import com.wutsi.checkout.manager.dto.TransactionSummary
import com.wutsi.enums.TransactionType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.payment.core.Status
import com.wutsi.regulation.Country
import org.springframework.context.i18n.LocaleContextHolder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class TransactionWidget(
    private val paymentProviderLogoUrl: String,
    private val orderId: String?,
    private val dateTime: OffsetDateTime,
    private val type: TransactionType,
    private val status: Status,
    private val amount: Long,
    private val country: Country,
    private val merchant: Boolean,
    private val action: Action?,
) : CompositeWidgetAware() {
    companion object {
        fun of(tx: TransactionSummary, country: Country, action: Action?, merchant: Boolean, timezoneId: String?) =
            TransactionWidget(
                paymentProviderLogoUrl = tx.paymentMethod.provider.logoUrl,
                orderId = tx.orderId?.takeLast(4),
                dateTime = timezoneId?.let { DateTimeUtil.convert(tx.created, it) } ?: tx.created,
                type = TransactionType.valueOf(tx.type),
                status = Status.valueOf(tx.status),
                amount = tx.amount,
                country = country,
                merchant = merchant,
                action = action,
            )
    }

    override fun toWidgetAware(): WidgetAware {
        val moneyFormat = country.createMoneyFormat()
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormatShort, LocaleContextHolder.getLocale())

        return ListItem(
            leading = Text(dateTime.format(dateFormat)),
            trailing = Column(
                mainAxisAlignment = MainAxisAlignment.end,
                crossAxisAlignment = CrossAxisAlignment.end,
                children = listOf(
                    Container(
                        padding = 1.0,
                        child = Text(
                            caption = toAmountSign() + moneyFormat.format(amount),
                            bold = type == TransactionType.CASHOUT,
                            alignment = TextAlignment.Right,
                            color = getAmountColor(),
                        ),
                    ),
                    Container(
                        padding = 1.0,
                        child = Image(
                            width = 24.0,
                            height = 24.0,
                            url = paymentProviderLogoUrl,
                        ),
                    ),
                ),
            ),
            caption = getCaption(),
            subCaption = getText("transaction.status.$status"),
            action = action,
        )
    }

    private fun getCaption(): String =
        if (type == TransactionType.CHARGE && orderId != null) {
            getText("widget.transaction.order_id", arrayOf(orderId))
        } else {
            getText("transaction.type.$type")
        }

    private fun toAmountSign(): String =
        if (merchant) {
            if (type == TransactionType.CASHOUT) {
                "-"
            } else {
                "+"
            }
        } else {
            ""
        }

    private fun getAmountColor() = if (merchant) {
        when (status) {
            Status.FAILED -> Theme.COLOR_DANGER
            Status.PENDING -> Theme.COLOR_WARNING
            Status.SUCCESSFUL -> Theme.COLOR_BLACK
            else -> null
        }
    } else {
        null
    }
}
