package com.wutsi.application.widget

import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize

class GridWidget(
    private val children: List<OfferWidget>,
    private val columns: Int = 2,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware {
        val productRows = toProductRows()
        return Column(
            crossAxisAlignment = CrossAxisAlignment.start,
            mainAxisAlignment = MainAxisAlignment.start,
            children = productRows.map {
                toRowWidget(it)
            },
        )
    }

    private fun toRowWidget(items: List<WidgetAware>): WidgetAware {
        val widgets = mutableListOf<WidgetAware>()
        widgets.addAll(items)
        while (widgets.size < columns) // Padding
            widgets.add(Container())

        return Row(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            mainAxisSize = MainAxisSize.min,
            children = widgets.map {
                Flexible(child = it)
            },
        )
    }

    private fun toProductRows(): List<List<WidgetAware>> {
        val rows = mutableListOf<List<WidgetAware>>()
        var cur = mutableListOf<WidgetAware>()
        children.forEach {
            cur.add(it)
            if (cur.size == columns) {
                rows.add(cur)
                cur = mutableListOf()
            }
        }
        if (cur.isNotEmpty()) {
            rows.add(cur)
        }
        return rows
    }
}
