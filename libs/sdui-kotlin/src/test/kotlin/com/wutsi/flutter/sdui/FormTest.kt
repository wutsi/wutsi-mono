package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class FormTest {
    @Test
    fun toWidget() {
        val form = Form(
            children = listOf(Page(url = "xxx"), Container()),
        )

        val widget = form.toWidget()

        assertEquals(WidgetType.Form, widget.type)
        assertNull(widget.action)

        assertTrue(widget.attributes.isEmpty())

        assertEquals(2, widget.children.size)
    }
}
