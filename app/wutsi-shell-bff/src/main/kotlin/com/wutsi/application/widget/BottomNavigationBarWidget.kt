package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.widget.WidgetL10n.getText
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.BottomNavigationBar
import com.wutsi.flutter.sdui.BottomNavigationBarItem
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType

class BottomNavigationBarWidget(
    private val profileUrl: String? = null,
    private val ordersUrl: String? = null,
    private val transactionsUrl: String? = null,
    private val storeId: Long? = null,
    private val donationUrl: String? = null,
) : CompositeWidgetAware() {
    override fun toWidgetAware(): WidgetAware = toBottomNavigationBar()

    fun toBottomNavigationBar() = BottomNavigationBar(
        background = Theme.COLOR_PRIMARY,
        selectedItemColor = Theme.COLOR_WHITE,
        unselectedItemColor = Theme.COLOR_WHITE,
        items = listOfNotNull(
            BottomNavigationBarItem(
                icon = Theme.ICON_HOME,
                caption = getText("widget.bottom-nav-bar.home"),
                action = Action(
                    type = ActionType.Route,
                    url = "route:/~",
                ),
            ),
            profileUrl?.let {
                BottomNavigationBarItem(
                    icon = storeId?.let { Theme.ICON_STORE } ?: Theme.ICON_PERSON,
                    caption = storeId?.let { getText("widget.bottom-nav-bar.store") }
                        ?: getText("widget.bottom-nav-bar.profile"),
                    action = Action(
                        type = ActionType.Route,
                        url = it,
                    ),
                )
            },
            transactionsUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_HISTORY,
                    caption = getText("widget.bottom-nav-bar.transactions"),
                    action = Action(
                        type = ActionType.Route,
                        url = it,
                    ),
                )
            },
            ordersUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_ORDER,
                    caption = getText("widget.bottom-nav-bar.orders"),
                    action = Action(
                        type = ActionType.Route,
                        url = it,
                    ),
                )
            },
            donationUrl?.let {
                BottomNavigationBarItem(
                    icon = Theme.ICON_VOLUNTEER,
                    caption = getText("widget.bottom-nav-bar.donations"),
                    action = Action(
                        type = ActionType.Route,
                        url = it,
                    ),
                )
            },
        ),
    )
}
