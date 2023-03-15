package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class AppBarTest {
    @Test
    fun toWidget() {
        val obj = AppBar(
            title = "Screen1",
            actions = listOf(IconButton("a"), IconButton("b")),
            elevation = 1.0,
            foregroundColor = "#ffffff",
            backgroundColor = "#ff0000",
            leading = IconButton("c"),
            automaticallyImplyLeading = false,
            bottom = TabBar(tabs = listOf(Container())),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.AppBar, widget.type)

        assertEquals(8, widget.attributes.size)
        assertEquals(obj.title, widget.attributes["title"])
        assertEquals(obj.elevation, widget.attributes["elevation"])
        assertEquals(obj.foregroundColor, widget.attributes["foregroundColor"])
        assertEquals(obj.backgroundColor, widget.attributes["backgroundColor"])
        assertEquals(obj.automaticallyImplyLeading, widget.attributes["automaticallyImplyLeading"])

        val actions = (widget.attributes["actions"]) as List<Widget>
        assertEquals(2, actions.size)
        assertEquals("a", actions[0].attributes["icon"])
        assertEquals(WidgetType.IconButton, actions[0].type)
        assertEquals("b", actions[1].attributes["icon"])
        assertEquals(WidgetType.IconButton, actions[1].type)

        val leading = (widget.attributes["leading"]) as Widget
        assertEquals(WidgetType.IconButton, leading.type)
        assertEquals("c", leading.attributes["icon"])

        val bottom = (widget.attributes["bottom"]) as Widget
        assertEquals(WidgetType.TabBar, bottom.type)

        assertNull(widget.action)
    }
}
