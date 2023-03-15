package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class TabBar(
    val tabs: List<WidgetAware>,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.TabBar,
        children = tabs.map { it.toWidget() },
    )
}
