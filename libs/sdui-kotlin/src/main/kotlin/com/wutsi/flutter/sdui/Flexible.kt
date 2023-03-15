package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.FlexFit
import com.wutsi.flutter.sdui.enums.FlexFit.Tight
import com.wutsi.flutter.sdui.enums.WidgetType.Flexible

class Flexible(
    val flex: Int = 1,
    val fit: FlexFit = Tight,
    val child: WidgetAware? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Flexible,
        attributes = mapOf(
            "flex" to flex,
            "fit" to fit,
        ),
        children = child?.let { listOf(it.toWidget()) } ?: emptyList(),
    )
}
