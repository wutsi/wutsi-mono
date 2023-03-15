package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType

data class Widget(
    val type: WidgetType? = null,
    val attributes: Map<String, Any?> = emptyMap(),
    val children: List<Widget> = emptyList(),
    val action: Action? = null,
    val appBar: Widget? = null,
    val floatingActionButton: Widget? = null,
    val bottomNavigationBar: Widget? = null,
)
