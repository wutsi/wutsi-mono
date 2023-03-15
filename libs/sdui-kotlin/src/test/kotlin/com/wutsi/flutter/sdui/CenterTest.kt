package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CenterTest {
    @Test
    fun toWidget() {
        val obj = Center()

        val widget = obj.toWidget()
        assertEquals(WidgetType.Center, widget.type)
        assertNull(widget.action)
        assertEquals(0, widget.attributes.size)
    }
}
