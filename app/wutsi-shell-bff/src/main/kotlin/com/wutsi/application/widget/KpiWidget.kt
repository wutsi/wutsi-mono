package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.regulation.Country

class KpiWidget(
    private val name: String,
    private val value: Long,
    private val country: Country,
    private val money: Boolean = false,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware {
        return Container(
            child = Row(
                children = listOf(
                    Flexible(
                        child = Container(
                            padding = 10.0,
                            child = if (money) {
                                MoneyText(
                                    numberFormat = country.numberFormat,
                                    currency = country.currencySymbol,
                                    color = Theme.COLOR_PRIMARY,
                                    valueFontSize = Theme.TEXT_SIZE_X_LARGE,
                                    bold = true,
                                    value = value.toDouble(),
                                    alignment = TextAlignment.Right,
                                    locale = country.locale,
                                )
                            } else {
                                Text(
                                    caption = country.createNumberFormat().format(value),
                                    size = Theme.TEXT_SIZE_X_LARGE,
                                    color = Theme.COLOR_PRIMARY,
                                    bold = true,
                                    alignment = TextAlignment.Right,
                                )
                            },
                        ),
                    ),
                    Flexible(
                        child = Container(
                            padding = 10.0,
                            child = Text(name),
                        ),
                    ),
                ),
            ),
        )
    }
}
