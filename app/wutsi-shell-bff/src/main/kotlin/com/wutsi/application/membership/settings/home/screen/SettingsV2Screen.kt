package com.wutsi.application.membership.settings.home.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.widget.AvatarWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2")
class SettingsV2Screen : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val user = getCurrentMember()
        return Screen(
            id = Page.SETTINGS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.app-bar.title"),
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = listOfNotNull(
                        /* Avatar */
                        Container(
                            padding = 5.0,
                            child = Column(
                                mainAxisSize = MainAxisSize.min,
                                crossAxisAlignment = CrossAxisAlignment.center,
                                mainAxisAlignment = MainAxisAlignment.spaceAround,
                                children = listOfNotNull(
                                    AvatarWidget(
                                        radius = 32.0,
                                        displayName = user.displayName,
                                        pictureUrl = user.pictureUrl,
                                    ),
                                    Button(
                                        type = ButtonType.Text,
                                        caption = getText("page.settings.button.change-picture"),
                                        action = Action(
                                            type = Route,
                                            url = urlBuilder.build("${Page.getSettingsUrl()}/picture"),
                                        ),
                                        padding = 10.0,
                                    ),
                                    Text(
                                        caption = user.displayName,
                                        alignment = TextAlignment.Center,
                                        size = Theme.TEXT_SIZE_LARGE,
                                        bold = true,
                                    ),

                                    if (user.business) {
                                        Container(
                                            padding = 10.0,
                                            child = Text(
                                                caption = getText("page.settings.business-account"),
                                                alignment = TextAlignment.Center,
                                                color = Theme.COLOR_GRAY,
                                            ),
                                        )
                                    } else {
                                        null
                                    },

                                    formattedPhoneNumber(user.phoneNumber)?.let {
                                        Text(
                                            caption = it,
                                            alignment = TextAlignment.Center,
                                        )
                                    },
                                ),
                            ),
                        ),

                        /* Profile */
                        listItem(
                            "page.settings.listitem.personal.caption",
                            urlBuilder.build("${Page.getSettingsUrl()}/profile"),
                            icon = Theme.ICON_PERSON,
                        ),
                        listItem(
                            "page.settings.listitem.account.caption",
                            urlBuilder.build(Page.getSettingsAccountListUrl()),
                            icon = Theme.ICON_PAYMENT,
                        ),

                        /* Business Apps */
                        if (user.business) {
                            Container(padding = 20.0)
                        } else {
                            null
                        },

                        /* Business Apps - Store */
                        if (user.business) {
                            if (user.storeId != null) {
                                listItem(
                                    "page.settings.listitem.store.caption",
                                    urlBuilder.build(Page.getSettingsStoreUrl()),
                                    icon = Theme.ICON_STORE,
                                )
                            } else {
                                listItemSwitch(
                                    "page.settings.listitem.activate-store.caption",
                                    urlBuilder.build("${Page.getSettingsUrl()}/enable-store"),
                                    subCaption = "page.settings.listitem.store.sub-caption",
                                )
                            }
                        } else {
                            null
                        },

                        /* Others */
                        Container(padding = 20.0),
                        listItem(
                            "page.settings.listitem.security.caption",
                            urlBuilder.build("${Page.getSecurityUrl()}"),
                            icon = Theme.ICON_LOCK,
                        ),
                        listItem(
                            "page.settings.listitem.about.caption",
                            urlBuilder.build("${Page.getAboutUrl()}"),
                            icon = Theme.ICON_INFO,
                        ),
                        listItem(
                            caption = "page.settings.button.logout",
                            icon = Theme.ICON_LOGOUT,
                            showTrailingIcon = false,
                            action = Action(
                                type = Command,
                                url = urlBuilder.build("${Page.getSettingsUrl()}/logout"),
                                replacement = true,
                                prompt = Dialog(
                                    type = DialogType.Confirm,
                                    title = getText("prompt.confirm.title"),
                                    message = getText("page.settings.confirm.logout"),
                                ).toWidget(),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/logout")
    fun logout(): Action {
        val member = getCurrentMember()

        securityManagerApi.logout()
        return gotoLogin(phoneNumber = member.phoneNumber, hideChangeAccount = false)
    }

    @PostMapping("/enable-store")
    fun enableStore(): Action =
        gotoUrl(
            urlBuilder.build(Page.getSettingsStoreActivateUrl()),
        )

    private fun listItem(caption: String, url: String, icon: String? = null) = listItem(
        caption = caption,
        icon = icon,
        action = Action(
            type = Route,
            url = url,
        ),
    )

    private fun listItem(caption: String, action: Action, icon: String? = null, showTrailingIcon: Boolean = true) =
        ListItem(
            padding = 5.0,
            leading = icon?.let { Icon(code = icon, size = 24.0, color = Theme.COLOR_PRIMARY) },
            caption = getText(caption),
            trailing = if (showTrailingIcon) {
                Icon(
                    code = Theme.ICON_CHEVRON_RIGHT,
                    size = 24.0,
                )
            } else {
                null
            },
            action = action,
        )

    private fun listItemSwitch(
        caption: String,
        url: String,
        icon: String? = null,
        selected: Boolean = false,
        subCaption: String? = null,
    ) = ListItemSwitch(
        name = "value",
        icon = icon,
        caption = getText(caption),
        subCaption = subCaption?.let { getText(subCaption) },
        selected = selected,
        action = Action(
            type = Command,
            url = url,
        ),
    )
}
