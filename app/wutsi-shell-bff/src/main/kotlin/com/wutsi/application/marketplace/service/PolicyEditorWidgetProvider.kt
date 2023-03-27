package com.wutsi.application.marketplace.service

import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.dto.Store
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale
import kotlin.math.max

@Service
class PolicyEditorWidgetProvider(
    private val messages: MessageSource,
) {
    fun get(name: String, store: Store): WidgetAware =
        when (name) {
            "cancellation-message" -> getInputWidget(store.cancellationPolicy.message, 160)
            "cancellation-window" -> getCancellationWindowWidget(store.cancellationPolicy.window)
            "return-message" -> getInputWidget(store.returnPolicy.message, 160)
            "return-contact-window" -> getReturnWindow(store.returnPolicy.contactWindow)
            "return-ship-back-window" -> getReturnWindow(store.returnPolicy.shipBackWindow)

            else -> throw IllegalStateException("Not supported: $name")
        }

    private fun getReturnWindow(value: Int) =
        DropdownButton(
            name = "value",
            value = max(1 * 24, value).toString(),
            children = IntRange(1, 30).map {
                DropdownMenuItem(
                    value = (it * 24).toString(),
                    caption = if (it == 1) getText("1_day") else getText("n_days", arrayOf(it)),
                )
            },
        )

    private fun getCancellationWindowWidget(value: Int) =
        DropdownButton(
            name = "value",
            value = max(1, value).toString(),
            children = IntRange(1, 24).map {
                DropdownMenuItem(
                    value = it.toString(),
                    caption = if (it == 1) getText("1_hour") else getText("n_hours", arrayOf(it)),
                )
            },
        )

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
