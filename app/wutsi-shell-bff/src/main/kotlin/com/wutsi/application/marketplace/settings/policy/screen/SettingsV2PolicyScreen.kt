package com.wutsi.application.marketplace.settings.policy.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/policies")
class SettingsV2PolicyScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val store = marketplaceManagerApi.getStore(member.storeId!!).store

        return Screen(
            id = Page.SETTINGS_POLICY,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.policy.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Flexible(
                        child = ListView(
                            separatorColor = Theme.COLOR_DIVIDER,
                            separator = true,
                            children = listOfNotNull(
                                ListItemSwitch(
                                    name = "value",
                                    selected = store.cancellationPolicy.accepted,
                                    caption = getText("page.settings.policy.attribute.cancellation-accepted"),
                                    action = executeCommand(
                                        url = urlBuilder.build("${Page.getSettingsPoliciesUrl()}/toggle"),
                                        parameters = mapOf(
                                            "name" to "cancellation-accepted",
                                            "value" to store.cancellationPolicy.accepted.toString(),
                                        ),
                                    ),
                                ),

                                if (store.cancellationPolicy.accepted) {
                                    toListItemWidget(
                                        "page.settings.policy.attribute.cancellation-window",
                                        getText(
                                            if (store.cancellationPolicy.window == 1) "1_hour" else "n_hours",
                                            arrayOf(store.cancellationPolicy.window),
                                        ),
                                        urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}?name=cancellation-window"),
                                    )
                                } else {
                                    null
                                },

                                toListItemWidget(
                                    "page.settings.policy.attribute.cancellation-message",
                                    shortText(store.cancellationPolicy.message),
                                    urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}?name=cancellation-message"),
                                ),

                                ListItemSwitch(
                                    name = "value",
                                    selected = store.returnPolicy.accepted,
                                    caption = getText("page.settings.policy.attribute.return-accepted"),
                                    action = executeCommand(
                                        url = urlBuilder.build("${Page.getSettingsPoliciesUrl()}/toggle"),
                                        parameters = mapOf(
                                            "name" to "return-accepted",
                                            "value" to store.returnPolicy.accepted.toString(),
                                        ),
                                    ),
                                ),

                                if (store.returnPolicy.accepted) {
                                    toListItemWidget(
                                        "page.settings.policy.attribute.return-contact-window",
                                        getText(
                                            if (store.returnPolicy.contactWindow == 1) "1_day" else "n_days",
                                            arrayOf(store.returnPolicy.contactWindow / 24),
                                        ),
                                        urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}?name=return-contact-window"),
                                    )
                                } else {
                                    null
                                },

                                if (store.returnPolicy.accepted) {
                                    toListItemWidget(
                                        "page.settings.policy.attribute.return-ship-back-window",
                                        getText(
                                            if (store.returnPolicy.shipBackWindow == 1) "1_day" else "n_days",
                                            arrayOf(store.returnPolicy.shipBackWindow / 24),
                                        ),
                                        urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}?name=return-ship-back-window"),
                                    )
                                } else {
                                    null
                                },

                                toListItemWidget(
                                    "page.settings.policy.attribute.return-message",
                                    shortText(store.returnPolicy.message),
                                    urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}?name=return-message"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/toggle")
    fun toggle(
        @RequestParam name: String,
        @RequestParam value: Boolean,
    ): Action {
        val member = getCurrentMember()
        marketplaceManagerApi.updateStorePolicyAttribute(
            id = member.storeId!!,
            request = UpdateStorePolicyAttributeRequest(
                name = name,
                value = (!value).toString(),
            ),
        )
        return gotoUrl(
            replacement = true,
            url = urlBuilder.build(Page.getSettingsPoliciesUrl()),
        )
    }

    private fun toListItemWidget(caption: String, value: String?, url: String) = ListItem(
        caption = getText(caption),
        subCaption = value,
        trailing = Icon(
            code = Theme.ICON_EDIT,
            size = 24.0,
            color = Theme.COLOR_BLACK,
        ),
        action = Action(
            type = ActionType.Route,
            url = url,
        ),
    )

    private fun shortText(value: String?): String? =
        if (value == null) {
            null
        } else if (value.length < 160) {
            value.replace('\n', ' ')
        } else {
            value.substring(0, 160).replace('\n', ' ') + "..."
        }
}
