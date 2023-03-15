package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType.Screen

class Screen(
    val id: String? = null,
    val appBar: AppBar? = null,
    val safe: Boolean = false,
    val child: WidgetAware? = null,
    val backgroundColor: String? = null,
    val floatingActionButton: WidgetAware? = null,
    val bottomNavigationBar: BottomNavigationBar? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Screen,
        children = child?.let { listOf(it.toWidget()) } ?: emptyList(),
        attributes = mapOf(
            "id" to id,
            "safe" to safe,
            "backgroundColor" to backgroundColor,
        ),
        appBar = appBar?.toWidget(),
        floatingActionButton = floatingActionButton?.toWidget(),
        bottomNavigationBar = bottomNavigationBar?.toWidget(),
    )
}
