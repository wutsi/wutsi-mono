package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.FlexFit
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class FlexibleTest {
    @Test
    fun toWidget() {
        val flexible = Flexible(
            flex = 3,
            fit = FlexFit.Loose,
            child = Page(url = "xxx"),
        )

        val widget = flexible.toWidget()

        assertEquals(WidgetType.Flexible, widget.type)
        assertNull(widget.action)

        assertEquals(2, widget.attributes.size)
        assertEquals(flexible.fit, widget.attributes["fit"])
        assertEquals(flexible.flex, widget.attributes["flex"])

        assertEquals(1, widget.children.size)
    }
}
