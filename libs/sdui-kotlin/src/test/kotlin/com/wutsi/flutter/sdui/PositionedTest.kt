package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class PositionedTest {

    @Test
    fun toWidget() {
        val obj = Positioned(
            top = 1.0,
            bottom = 2.0,
            left = 3.0,
            right = 4.0,
            width = 10.0,
            height = 20.0,
            child = Container(),
        )

        val widget = obj.toWidget()

        assertNull(widget.action)

        assertEquals(WidgetType.Positioned, widget.type)

        assertEquals(6, widget.attributes.size)
        assertEquals(obj.top, widget.attributes["top"])
        assertEquals(obj.bottom, widget.attributes["bottom"])
        assertEquals(obj.left, widget.attributes["left"])
        assertEquals(obj.right, widget.attributes["right"])
        assertEquals(obj.width, widget.attributes["width"])
        assertEquals(obj.height, widget.attributes["height"])

        assertEquals(1, widget.children.size)
    }
}
