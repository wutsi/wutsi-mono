package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class QrImageTest {

    @Test
    fun toWidget() {
        val group = QrImage(
            data = "This is the data",
            version = 1,
            size = 300.0,
            padding = 10.0,
            embeddedImageUrl = "http://img.com/1.png",
            embeddedImageSize = 64.0,
        )

        val widget = group.toWidget()

        assertEquals(WidgetType.QrImage, widget.type)
        assertNull(widget.action)

        assertEquals(6, widget.attributes.size)
        assertEquals(group.data, widget.attributes["data"])
        assertEquals(group.version, widget.attributes["version"])
        assertEquals(group.size, widget.attributes["size"])
        assertEquals(group.padding, widget.attributes["padding"])
        assertEquals(group.embeddedImageUrl, widget.attributes["embeddedImageUrl"])
        assertEquals(group.embeddedImageSize, widget.attributes["embeddedImageSize"])

        assertEquals(0, widget.children.size)
    }
}
