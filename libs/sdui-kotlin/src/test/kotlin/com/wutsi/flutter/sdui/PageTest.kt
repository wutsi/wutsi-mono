package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class PageTest {
    @Test
    fun toWidget() {
        val page = Page(url = "https://www.google.ca", id = "xxx")

        val widget = page.toWidget()

        assertEquals(WidgetType.Page, widget.type)
        assertNull(widget.action)

        assertEquals(2, widget.attributes.size)
        assertEquals(page.id, widget.attributes["id"])
        assertEquals(page.url, widget.attributes["url"])

        assertEquals(0, widget.children.size)
    }
}
