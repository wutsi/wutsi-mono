package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.flutter.sdui.enums.WidgetType

class MoneyText(
    val value: Double,
    val currency: String,
    val color: String? = null,
    val numberFormat: String? = null,
    val valueFontSize: Double? = null,
    val currencyFontSize: Double? = null,
    val bold: Boolean? = null,
    val id: String? = null,
    val alignment: TextAlignment? = null,
    val locale: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.MoneyText,
        attributes = mapOf(
            "id" to id,
            "value" to value,
            "color" to color,
            "currency" to currency,
            "numberFormat" to numberFormat,
            "bold" to bold,
            "valueFontSize" to valueFontSize,
            "currencyFontSize" to currencyFontSize,
            "alignment" to alignment,
            "locale" to locale,
        ),
    )
}
