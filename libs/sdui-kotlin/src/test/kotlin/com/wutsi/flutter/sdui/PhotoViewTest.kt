package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PhotoViewTest {

    @Test
    fun toWidget() {
        val obj = PhotoView(
            url = "http://img.com/1.png",
        )

        val widget = obj.toWidget()

        Assertions.assertEquals(WidgetType.PhotoView, widget.type)
        Assertions.assertNull(widget.action)

        Assertions.assertEquals(1, widget.attributes.size)
        Assertions.assertEquals(obj.url, widget.attributes["url"])

        Assertions.assertEquals(0, widget.children.size)
    }
}
