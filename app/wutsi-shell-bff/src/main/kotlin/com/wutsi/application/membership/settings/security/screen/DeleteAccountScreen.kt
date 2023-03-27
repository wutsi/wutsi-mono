package com.wutsi.application.membership.settings.security.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.membership.manager.dto.Member
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/delete")
class DeleteAccountScreen : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val user = getCurrentMember()
        return Screen(
            id = Page.SECURITY_DELETE,
            appBar = AppBar(
                elevation = 0.0,
                title = getText("page.settings.delete-wallet.app-bar.title"),
                foregroundColor = Theme.COLOR_BLACK,
                backgroundColor = Theme.COLOR_WHITE,
            ),
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Row(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = Icon(
                                        code = Theme.ICON_CANCEL,
                                        color = Theme.COLOR_DANGER,
                                        size = 32.0,
                                    ),
                                ),
                                Container(
                                    alignment = Alignment.CenterLeft,
                                    child = Text(
                                        caption = getText("page.settings.delete-wallet.confirmation"),
                                        alignment = TextAlignment.Center,
                                        size = Theme.TEXT_SIZE_LARGE,
                                        bold = true,
                                    ),
                                ),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Container(
                                alignment = Alignment.CenterLeft,
                                padding = 10.0,
                                child = Text(getText("page.settings.delete-wallet.sub-title")),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Column(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.start,
                                children = IntRange(1, 5).map {
                                    Container(
                                        alignment = Alignment.CenterLeft,
                                        padding = 10.0,
                                        child = Text(getText("page.settings.delete-wallet.impact-$it")),
                                    )
                                },
                            ),
                        ),
                        Container(
                            padding = 20.0,
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.settings.delete-wallet.button.delete"),
                                action = Action(
                                    type = ActionType.Route,
                                    url = urlBuilder.build(getConfirmationUrl(user)),
                                ),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                type = ButtonType.Text,
                                caption = getText("page.settings.delete-wallet.button.not-now"),
                                action = gotoPreviousScreen(),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/done")
    fun done(): Widget =
        Screen(
            id = Page.SECURITY_DELETE,
            appBar = AppBar(
                elevation = 0.0,
                title = getText("page.settings.delete-wallet.app-bar.title"),
                foregroundColor = Theme.COLOR_BLACK,
                backgroundColor = Theme.COLOR_WHITE,
                automaticallyImplyLeading = false,
            ),
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.center,
                    crossAxisAlignment = CrossAxisAlignment.center,
                    children = listOf(
                        Container(
                            padding = 20.0,
                            alignment = Alignment.Center,
                            child = Icon(
                                code = Theme.ICON_CHECK,
                                color = Theme.COLOR_SUCCESS,
                                size = 80.0,
                            ),
                        ),
                        Container(
                            alignment = Alignment.Center,
                            child = Text(
                                caption = getText("page.settings.delete-wallet.done"),
                                alignment = TextAlignment.Center,
                                size = Theme.TEXT_SIZE_LARGE,
                                color = Theme.COLOR_PRIMARY,
                                bold = true,
                            ),
                        ),
                        Container(
                            padding = 20.0,
                        ),
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.settings.delete-wallet.button.done"),
                                action = gotoOnboard(),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()

    @PostMapping("/submit")
    fun submit(): Action {
        membershipManagerApi.deleteMember()
        return gotoUrl(
            urlBuilder.build("${Page.getSecurityUrl()}/delete/done"),
        )
    }

    private fun getConfirmationUrl(me: Member): String {
        return "${Page.getLoginUrl()}?phone=" + encodeURLParam(me.phoneNumber) +
            "&title=" + encodeURLParam(getText("page.settings.delete-wallet.app-bar.title")) +
            "&sub-title=" + encodeURLParam(getText("page.settings.delete-wallet.pin")) +
            "&auth=false" +
            "&return-to-route=false" +
            "&dark-mode=true" +
            "&return-url=" + encodeURLParam(urlBuilder.build("${Page.getSecurityUrl()}/delete/submit"))
    }
}
