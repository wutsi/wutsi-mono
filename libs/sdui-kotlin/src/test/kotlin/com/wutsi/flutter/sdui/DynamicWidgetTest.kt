package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DynamicWidgetTest {

    @Test
    fun toWidget() {
        val obj = DynamicWidget(
            url = "https://www.google.com",
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.DynamicWidget, widget.type)
        assertNull(widget.action)

        assertEquals(1, widget.attributes.size)
        assertEquals(obj.url, widget.attributes["url"])

        assertEquals(0, widget.children.size)
    }
}
