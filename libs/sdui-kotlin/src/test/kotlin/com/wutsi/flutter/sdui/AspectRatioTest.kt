package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class AspectRatioTest {
    @Test
    fun toWidget() {
        val obj = AspectRatio(3.0 / 2.0)

        val widget = obj.toWidget()
        assertEquals(WidgetType.AspectRatio, widget.type)
        assertNull(widget.action)
        assertEquals(1, widget.attributes.size)
        assertEquals(obj.aspectRatio, widget.attributes["aspectRatio"])
    }
}
