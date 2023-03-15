package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DividerTest {

    @Test
    fun toWidget() {
        val obj = Divider(height = 11.0, color = "xx")

        val widget = obj.toWidget()

        assertEquals(WidgetType.Divider, widget.type)
        kotlin.test.assertNull(widget.action)

        assertEquals(2, widget.attributes.size)
        assertEquals(obj.height, widget.attributes["height"])
        assertEquals(obj.color, widget.attributes["color"])

        assertEquals(0, widget.children.size)
    }
}
