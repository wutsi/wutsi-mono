package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.ButtonType

class PictureListViewWidget(
    private val children: List<PictureWidget>,
    private val action: Action? = null,
) : CompositeWidgetAware() {
    companion object {
        const val IMAGE_WIDTH = 150.0
        const val IMAGE_HEIGHT = 150.0
        const val IMAGE_PADDING = 2.0
    }

    override fun toWidgetAware(): WidgetAware {
        val images = mutableListOf<WidgetAware>()
        images.addAll(children)
        if (action != null) {
            images.add(
                Container(
                    background = Theme.COLOR_PRIMARY_LIGHT,
                    borderColor = Theme.COLOR_GRAY,
                    padding = IMAGE_PADDING,
                    width = IMAGE_WIDTH,
                    height = IMAGE_HEIGHT,
                    alignment = Alignment.Center,
                    child = Button(
                        type = ButtonType.Text,
                        icon = Theme.ICON_ADD,
                        iconColor = Theme.COLOR_PRIMARY,
                        iconSize = 32.0,
                        action = action,
                    ),
                ),
            )
        }
        return ListView(
            direction = Axis.Horizontal,
            children = images,
        )
    }
}
