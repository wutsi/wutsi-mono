package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class MoneyWithSlider(
    val name: String,
    val maxValue: Int,
    val maxLength: Int? = null,
    val value: Int? = null,
    val currency: String? = null,
    val sliderColor: String? = null,
    val moneyColor: String? = null,
    val numberFormat: String? = null,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.MoneyWithSlider,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "sliderColor" to sliderColor,
            "moneyColor" to moneyColor,
            "maxValue" to maxValue,
            "maxLength" to maxLength,
            "value" to value,
            "currency" to currency,
            "numberFormat" to numberFormat,
        ),
    )
}
