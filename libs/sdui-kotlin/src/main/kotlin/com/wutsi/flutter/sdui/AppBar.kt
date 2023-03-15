package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.AppBar

class AppBar(
    val title: String? = null,
    val elevation: Double? = null,
    val backgroundColor: String? = null,
    val foregroundColor: String? = null,
    val leading: WidgetAware? = null,
    val actions: List<WidgetAware>? = null,
    val automaticallyImplyLeading: Boolean? = null,
    val bottom: TabBar? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = AppBar,
        attributes = mapOf(
            "title" to title,
            "elevation" to elevation,
            "backgroundColor" to backgroundColor,
            "foregroundColor" to foregroundColor,
            "automaticallyImplyLeading" to automaticallyImplyLeading,
            "actions" to actions?.map { it.toWidget() },
            "leading" to leading?.toWidget(),
            "bottom" to bottom?.toWidget(),
        ),
    )
}
