package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.WidgetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class MoneyWithKeyboardTest {
    @Test
    fun toWidget() {
        val input = MoneyWithKeyboard(
            moneyColor = "1111",
            name = "foo",
            maxLength = 7,
            keyboardButtonSize = 90.0,
            currency = "XAF",
            value = 10000,
            keyboardColor = "2222",
            numberFormat = "xxx",
            id = "111",
        )

        val widget = input.toWidget()

        assertEquals(WidgetType.MoneyWithKeyboard, widget.type)
        assertNull(widget.action)

        assertEquals(9, widget.attributes.size)
        assertEquals(input.id, widget.attributes["id"])
        assertEquals(input.value, widget.attributes["value"])
        assertEquals(input.moneyColor, widget.attributes["moneyColor"])
        assertEquals(input.keyboardColor, widget.attributes["keyboardColor"])
        assertEquals(input.name, widget.attributes["name"])
        assertEquals(input.maxLength, widget.attributes["maxLength"])
        assertEquals(input.currency, widget.attributes["currency"])
        assertEquals(input.keyboardButtonSize, widget.attributes["keyboardButtonSize"])
        assertEquals(input.numberFormat, widget.attributes["numberFormat"])

        assertEquals(0, widget.children.size)
    }
}
