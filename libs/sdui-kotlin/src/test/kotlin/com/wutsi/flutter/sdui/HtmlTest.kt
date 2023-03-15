package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class HtmlTest {
    @Test
    fun toWidget() {
        val obj = Html(
            data = "<p>Hello</p>",
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.Html, widget.type)
        assertNull(widget.action)

        assertEquals(1, widget.attributes.size)
        assertEquals(obj.data, widget.attributes["data"])

        assertEquals(0, widget.children.size)
    }
}
