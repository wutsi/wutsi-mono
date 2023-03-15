package com.wutsi.flutter.sdui

import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.WidgetType.Input

class Input(
    val id: String? = null,
    val name: String,
    val value: String? = null,
    val hideText: Boolean = false,
    val required: Boolean = false,
    val caption: String = "",
    val hint: String? = null,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val type: InputType = InputType.Text,
    val maxLines: Int? = null,
    val maxLength: Int? = null,
    val countries: List<String>? = null,
    val minLength: Int = 0,
    val action: Action? = null,
    val uploadUrl: String? = null,
    val imageSource: ImageSource? = null,
    val imageMaxWidth: Int? = null,
    val imageMaxHeight: Int? = null,
    val videoMaxDuration: Int? = null,
    val prefix: String? = null,
    val suffix: String? = null,
    val initialCountry: String? = null,
    val inputFormatterRegex: String? = null,
) : WidgetAware {
    override fun toWidget() = Widget(
        type = Input,
        attributes = mapOf(
            "id" to id,
            "name" to name,
            "value" to value,
            "hideText" to hideText,
            "required" to required,
            "caption" to caption,
            "hint" to hint,
            "enabled" to enabled,
            "readOnly" to readOnly,
            "type" to type,
            "maxLines" to maxLines,
            "maxLength" to maxLength,
            "minLength" to minLength,
            "countries" to countries,
            "uploadUrl" to uploadUrl,
            "imageSource" to imageSource,
            "videoMaxDuration" to videoMaxDuration,
            "imageMaxHeight" to imageMaxHeight,
            "imageMaxWidth" to imageMaxWidth,
            "prefix" to prefix,
            "suffix" to suffix,
            "initialCountry" to initialCountry,
            "inputFormatterRegex" to inputFormatterRegex,
        ),
        action = action,
    )
}
