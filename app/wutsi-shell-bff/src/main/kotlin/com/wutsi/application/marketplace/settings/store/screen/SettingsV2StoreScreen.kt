package com.wutsi.application.marketplace.settings.store.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store")
class SettingsV2StoreScreen : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_STORE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.store.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.CenterLeft,
                        child = Text(
                            caption = getText("page.settings.store.message"),
                            alignment = TextAlignment.Left,
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 2.0),
                    Flexible(
                        child = ListView(
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = listOfNotNull(
                                ListItem(
                                    caption = getText("page.settings.store.products"),
                                    leading = Icon(code = Theme.ICON_SHOPPING_BAG, color = Theme.COLOR_PRIMARY),
                                    trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                                    action = gotoUrl(
                                        urlBuilder.build(Page.getSettingsProductListUrl()),
                                    ),
                                ),
                                ListItem(
                                    caption = getText("page.settings.store.discounts"),
                                    leading = Icon(code = Theme.ICON_DISCOUNT, color = Theme.COLOR_PRIMARY),
                                    trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                                    action = gotoUrl(
                                        urlBuilder.build(Page.getSettingsDiscountListUrl()),
                                    ),
                                ),
                                ListItem(
                                    caption = getText("page.settings.store.policies"),
                                    leading = Icon(code = Theme.ICON_POLICY, color = Theme.COLOR_PRIMARY),
                                    trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                                    action = gotoUrl(
                                        urlBuilder.build(Page.getSettingsPoliciesUrl()),
                                    ),
                                ),
                                ListItem(
                                    caption = getText("page.settings.store.stats"),
                                    leading = Icon(code = Theme.ICON_BAR_CHART, color = Theme.COLOR_PRIMARY),
                                    trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
                                    action = gotoUrl(
                                        urlBuilder.build(Page.getSettingsStoreStats()),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }
}
