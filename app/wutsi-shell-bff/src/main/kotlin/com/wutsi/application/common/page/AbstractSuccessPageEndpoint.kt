package com.wutsi.application.common.page

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.WidgetAware

abstract class AbstractSuccessPageEndpoint : AbstractPageEndpoint() {
    override fun showHeader() = false

    override fun getBody(): WidgetAware? = null

    override fun getIcon() = Icon(
        code = Theme.ICON_CHECK,
        color = Theme.COLOR_SUCCESS,
        size = 80.0,
    )
}
