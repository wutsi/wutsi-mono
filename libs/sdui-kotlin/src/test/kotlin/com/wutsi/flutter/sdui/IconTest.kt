package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class IconTest {
    @Test
    fun toWidget() {
        val icon = Icon(
            code = "0erf",
            size = 100.0,
            color = "ff0000",
        )

        val widget = icon.toWidget()

        assertEquals(WidgetType.Icon, widget.type)
        assertNull(widget.action)

        assertEquals(3, widget.attributes.size)
        assertEquals(icon.code, widget.attributes["code"])
        assertEquals(icon.size, widget.attributes["size"])
        assertEquals(icon.color, widget.attributes["color"])

        assertEquals(0, widget.children.size)
    }
}
