package com.wutsi.application.widget

import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware

abstract class CompositeWidgetAware : WidgetAware {
    abstract fun toWidgetAware(): WidgetAware

    override fun toWidget(): Widget =
        toWidgetAware().toWidget()
}
