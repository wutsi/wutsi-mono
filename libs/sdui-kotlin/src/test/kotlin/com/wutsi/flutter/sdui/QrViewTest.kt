package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class QrViewTest {
    @Test
    fun toWidget() {
        val obj = QrView(
            submitUrl = "https://www.google.ca",
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.QrView, widget.type)
        assertNull(widget.action)

        assertEquals(1, widget.attributes.size)
        assertEquals(obj.submitUrl, widget.attributes["submitUrl"])

        assertEquals(0, widget.children.size)
    }
}
