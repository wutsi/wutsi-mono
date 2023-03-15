package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class TabBarView(
    val children: List<WidgetAware>,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.TabBarView,
        children = children.map { it.toWidget() },
    )
}
