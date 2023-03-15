package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ExpandablePanelTest {

    @Test
    fun toWidget() {
        val obj = ExpandablePanel(
            header = "Yo",
            collapsed = "Man",
            expanded = Text("This is a long text"),
        )

        val widget = obj.toWidget()

        assertEquals(WidgetType.ExpandablePanel, widget.type)
        assertEquals(2, widget.attributes.size)
        assertEquals(obj.header, widget.attributes["header"])
        assertEquals(obj.collapsed, widget.attributes["collapsed"])

        assertEquals(1, widget.children.size)
        assertEquals(WidgetType.Text, widget.children[0].type)

        assertNull(widget.action)
    }
}
