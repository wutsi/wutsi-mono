package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class Noop : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.Noop,
    )
}
