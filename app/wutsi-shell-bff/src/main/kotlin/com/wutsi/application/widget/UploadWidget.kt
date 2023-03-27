package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ImageSource
import com.wutsi.flutter.sdui.enums.InputType

class UploadWidget(
    private val name: String = "file",
    private val uploadUrl: String,
    private val action: Action? = null,
    private val imageMaxWidth: Int? = null,
    private val imageMaxHeight: Int? = null,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware =
        Column(
            children = listOf(
                Container(
                    borderColor = Theme.COLOR_PRIMARY,
                    border = 1.0,
                    child = Input(
                        name = name,
                        uploadUrl = uploadUrl,
                        type = InputType.Image,
                        imageSource = ImageSource.Camera,
                        caption = getText("widget.upload.camera"),
                        imageMaxWidth = imageMaxWidth,
                        imageMaxHeight = imageMaxHeight,
                        action = action,
                    ),
                ),
                Container(padding = 10.0),
                Container(
                    borderColor = Theme.COLOR_PRIMARY,
                    border = 1.0,
                    child = Input(
                        name = name,
                        uploadUrl = uploadUrl,
                        type = InputType.Image,
                        imageSource = ImageSource.Gallery,
                        caption = getText("widget.upload.gallery"),
                        imageMaxWidth = imageMaxWidth,
                        imageMaxHeight = imageMaxHeight,
                        action = action,
                    ),
                ),
            ),
        )

    private fun getText(key: String, args: Array<Any> = emptyArray()): String =
        WidgetL10n.getText(key, args)
}
