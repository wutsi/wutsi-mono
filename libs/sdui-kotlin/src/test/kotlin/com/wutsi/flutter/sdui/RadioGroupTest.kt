package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class RadioGroupTest {
    @Test
    fun toWidget() {
        val group = RadioGroup(
            id = "1111",
            name = "foo",
            value = "1",
            separator = true,
            separatorColor = "xxx",
            children = listOf(Radio("1"), Radio("2")),
            action = Action(),
        )

        val widget = group.toWidget()

        assertEquals(WidgetType.RadioGroup, widget.type)
        assertNotNull(widget.action)

        assertEquals(5, widget.attributes.size)
        Assertions.assertEquals(group.id, widget.attributes["id"])
        assertEquals(group.name, widget.attributes["name"])
        assertEquals(group.value, widget.attributes["value"])
        assertEquals(group.separator, widget.attributes["separator"])
        assertEquals(group.separatorColor, widget.attributes["separatorColor"])

        assertEquals(2, widget.children.size)
    }
}
