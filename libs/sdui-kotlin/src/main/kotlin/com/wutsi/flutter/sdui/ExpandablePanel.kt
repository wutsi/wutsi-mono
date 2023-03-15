package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

class ExpandablePanel(
    val header: String,
    val collapsed: String? = null,
    val expanded: WidgetAware,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = WidgetType.ExpandablePanel,
        attributes = mapOf(
            "header" to header,
            "collapsed" to collapsed,
        ),
        children = listOf(expanded.toWidget()),
    )
}
