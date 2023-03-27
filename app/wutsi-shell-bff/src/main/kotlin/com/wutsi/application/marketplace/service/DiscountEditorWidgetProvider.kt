package com.wutsi.application.marketplace.service

import com.wutsi.application.util.DateTimeUtil
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.dto.Discount
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class DiscountEditorWidgetProvider(
    private val messages: MessageSource,
) {
    companion object {
        private val RATES = listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70)
    }

    fun get(name: String, discount: Discount, timezoneId: String? = null): WidgetAware =
        when (name) {
            "name" -> get(name, discount.name)
            "starts" -> get(name, discount.starts, timezoneId)
            "ends" -> get(name, discount.ends, timezoneId)
            "rate" -> get(name, discount.rate)
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun get(name: String, value: Any?, timezoneId: String? = null): WidgetAware =
        when (name) {
            "name" -> getInputWidget(value, 30, required = true)
            "starts" -> getInputWidget(toDateText(value, timezoneId), type = InputType.Date)
            "ends" -> getInputWidget(toDateText(value, timezoneId), type = InputType.Date)
            "rate" -> DropdownButton(
                name = "value",
                hint = getText("page.settings.discount.attribute.rate"),
                value = if (!value.toString().isNullOrEmpty() && RATES.contains(
                        value.toString()
                            .toInt(),
                    )
                ) {
                    value?.toString()
                } else {
                    null
                },
                children = RATES.map {
                    DropdownMenuItem(
                        value = it.toString(),
                        caption = "-$it%",
                    )
                },
            )
            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun toDateText(value: Any?, timezoneId: String?): String? =
        if (value is OffsetDateTime) {
            DateTimeUtil.convert(value, timezoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else {
            null
        }

    private fun getInputWidget(
        value: Any?,
        maxlength: Int? = null,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false,
        decimal: Boolean? = null,
    ) =
        Input(
            name = "value",
            value = value?.toString(),
            type = type,
            maxLength = maxlength,
            maxLines = maxLines,
            required = required,
            inputFormatterRegex = if (decimal == false) "[0-9]" else null,
        )

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, getLocale())
}
