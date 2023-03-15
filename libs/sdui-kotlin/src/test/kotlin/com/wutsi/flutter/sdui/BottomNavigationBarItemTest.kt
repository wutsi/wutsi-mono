package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BottomNavigationBarItemTest {
    @Test
    fun toWidget() {
        val obj = BottomNavigationBarItem(
            icon = "0erf",
            caption = "foo",
            action = Action(type = ActionType.Route, url = "http://www.google.ca"),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.BottomNavigationBarItem, widget.type)
        assertEquals(obj.action, widget.action)

        assertEquals(2, widget.attributes.size)
        assertEquals(obj.icon, widget.attributes["icon"])
        assertEquals(obj.caption, widget.attributes["caption"])

        assertEquals(0, widget.children.size)
    }
}
