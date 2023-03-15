package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class BottomNavigationBarTest {
    @Test
    fun toWidget() {
        val obj = BottomNavigationBar(
            items = listOf(BottomNavigationBarItem("xxx", "", Action())),
            selectedItemColor = "111",
            unselectedItemColor = "222",
            background = "red",
            iconSize = 33.0,
            elevation = 22.0,
            currentIndex = 4,
            fontSize = 3.0,
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.BottomNavigationBar, widget.type)
        assertNull(widget.action)

        assertEquals(7, widget.attributes.size)
        assertEquals(obj.selectedItemColor, widget.attributes["selectedItemColor"])
        assertEquals(obj.unselectedItemColor, widget.attributes["unselectedItemColor"])
        assertEquals(obj.background, widget.attributes["background"])
        assertEquals(obj.iconSize, widget.attributes["iconSize"])
        assertEquals(obj.elevation, widget.attributes["elevation"])
        assertEquals(obj.currentIndex, widget.attributes["currentIndex"])
        assertEquals(obj.fontSize, widget.attributes["fontSize"])

        assertEquals(1, widget.children.size)
    }
}
