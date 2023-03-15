package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ExpandedTest {
    @Test
    fun toWidget() {
        val obj = Expanded(
            child = Container(),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.Expanded, widget.type)
        assertNull(widget.action)

        assertEquals(0, widget.attributes.size)

        assertEquals(1, widget.children.size)
    }
}
