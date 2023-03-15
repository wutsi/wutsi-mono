package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class ChipTest {

    @Test
    fun toWidget() {
        val obj = Chip(
            caption = "yo",
            backgroundColor = "eee",
            color = "fff",
            padding = 10.0,
            elevation = 1.0,
            shadowColor = "xxx",
            fontSize = 55.0,
            id = "111",
        )

        val widget = obj.toWidget()

        assertNull(widget.action)

        assertEquals(WidgetType.Chip, widget.type)

        assertEquals(8, widget.attributes.size)
        assertEquals(obj.id, widget.attributes["id"])
        assertEquals(obj.padding, widget.attributes["padding"])
        assertEquals(obj.color, widget.attributes["color"])
        assertEquals(obj.elevation, widget.attributes["elevation"])
        assertEquals(obj.shadowColor, widget.attributes["shadowColor"])
        assertEquals(obj.backgroundColor, widget.attributes["backgroundColor"])
        assertEquals(obj.caption, widget.attributes["caption"])
        assertEquals(obj.fontSize, widget.attributes["fontSize"])

        assertEquals(0, widget.children.size)
    }
}
