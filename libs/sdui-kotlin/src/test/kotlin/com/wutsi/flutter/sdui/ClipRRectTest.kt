package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ClipRRectTest {
    @Test
    fun toWidget() {
        val container = ClipRRect(
            borderRadius = 3.0,
            child = Page(url = "xxx"),
        )

        val widget = container.toWidget()

        assertEquals(WidgetType.ClipRRect, widget.type)
        assertNull(widget.action)

        assertEquals(1, widget.attributes.size)
        assertEquals(container.borderRadius, widget.attributes["borderRadius"])

        assertEquals(1, widget.children.size)
    }
}
