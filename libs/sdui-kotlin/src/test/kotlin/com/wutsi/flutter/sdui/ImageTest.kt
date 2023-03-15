package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class ImageTest {
    @Test
    fun toWidget() {
        val image = Image(
            id = "111",
            url = "https://img.com/1.png",
            width = 100.0,
            height = 200.0,
            fit = BoxFit.contain,
        )

        val widget = image.toWidget()

        assertEquals(WidgetType.Image, widget.type)
        assertNull(widget.action)

        assertEquals(5, widget.attributes.size)
        assertEquals(image.id, widget.attributes["id"])
        assertEquals(image.url, widget.attributes["url"])
        assertEquals(image.width, widget.attributes["width"])
        assertEquals(image.height, widget.attributes["height"])
        assertEquals(image.fit, widget.attributes["fit"])

        assertEquals(0, widget.children.size)
    }
}
