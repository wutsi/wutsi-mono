package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.WidgetType.Row

class Row(
    val mainAxisAlignment: MainAxisAlignment? = null,
    val mainAxisSize: MainAxisSize? = null,
    val crossAxisAlignment: CrossAxisAlignment? = null,
    val children: List<WidgetAware>,
    val id: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Row,
        attributes = mapOf(
            "id" to id,
            "mainAxisAlignment" to mainAxisAlignment,
            "mainAxisSize" to mainAxisSize,
            "crossAxisAlignment" to crossAxisAlignment,
        ),
        children = children.map { it.toWidget() },
    )
}
