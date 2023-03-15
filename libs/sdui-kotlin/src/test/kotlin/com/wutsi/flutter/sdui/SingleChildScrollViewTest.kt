package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SingleChildScrollViewTest {
    @Test
    fun toWidget() {
        val obj = SingleChildScrollView(
            child = Container(),
            padding = 10.0,
            primary = true,
            scrollDirection = Axis.Horizontal,
            reverse = false,
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.SingleChildScrollView, widget.type)
        assertNull(widget.action)

        assertEquals(4, widget.attributes.size)
        assertEquals(obj.padding, widget.attributes["padding"])
        assertEquals(obj.primary, widget.attributes["primary"])
        assertEquals(obj.reverse, widget.attributes["reverse"])
        assertEquals(obj.scrollDirection, widget.attributes["scrollDirection"])

        assertEquals(1, widget.children.size)
    }
}
