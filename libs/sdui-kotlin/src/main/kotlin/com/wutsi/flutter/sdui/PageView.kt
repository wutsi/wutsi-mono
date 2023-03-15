package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType.PageView

class PageView(
    val id: String? = null,
    val direction: Axis = Axis.Horizontal,
    val scrollOnUserInput: Boolean? = null,
    val children: List<Page>,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = PageView,
        children = children.map { it.toWidget() },
        attributes = mapOf(
            "id" to id,
            "direction" to direction,
            "scrollOnUserInput" to scrollOnUserInput,
        ),
    )
}
