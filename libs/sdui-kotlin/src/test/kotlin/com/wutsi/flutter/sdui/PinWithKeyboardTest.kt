package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PinWithKeyboardTest {
    @Test
    fun toWidget() {
        val button = PinWithKeyboard(
            hideText = true,
            color = "1111",
            name = "foo",
            action = Action(
                type = Prompt,
            ),
            maxLength = 7,
            pinSize = 11.0,
            keyboardButtonSize = 90.0,
            id = "111",
        )

        val widget = button.toWidget()

        assertEquals(WidgetType.PinWithKeyboard, widget.type)
        assertEquals(button.action, widget.action)

        assertEquals(7, widget.attributes.size)
        assertEquals(button.id, widget.attributes["id"])
        assertEquals(button.hideText, widget.attributes["hideText"])
        assertEquals(button.color, widget.attributes["color"])
        assertEquals(button.name, widget.attributes["name"])
        assertEquals(button.maxLength, widget.attributes["maxLength"])
        assertEquals(button.pinSize, widget.attributes["pinSize"])
        assertEquals(button.keyboardButtonSize, widget.attributes["keyboardButtonSize"])

        assertEquals(0, widget.children.size)
    }
}
