package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class MoneyWithKeyboard(
    val name: String,
    val maxLength: Int? = null,
    val value: Int? = null,
    val currency: String? = null,
    val keyboardColor: String? = null,
    val moneyColor: String? = null,
    val keyboardButtonSize: Double? = null,
    val numberFormat: String? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.MoneyWithKeyboard,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "keyboardColor" to keyboardColor,
            "moneyColor" to moneyColor,
            "maxLength" to maxLength,
            "value" to value,
            "currency" to currency,
            "keyboardButtonSize" to keyboardButtonSize,
            "numberFormat" to numberFormat,
        ),
    )
}
