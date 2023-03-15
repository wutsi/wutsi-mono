package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class NoopTest {
    @Test
    fun toWidget() {
        val obj = Noop()

        val widget = obj.toWidget()

        Assertions.assertEquals(WidgetType.Noop, widget.type)
        Assertions.assertNull(widget.action)

        Assertions.assertEquals(0, widget.attributes.size)

        Assertions.assertEquals(0, widget.children.size)
    }
}
