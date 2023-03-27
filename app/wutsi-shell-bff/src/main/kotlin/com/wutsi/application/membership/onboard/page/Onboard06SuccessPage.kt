package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/success")
class Onboard06SuccessPage : AbstractOnboardPage() {
    @PostMapping
    fun index(): Widget {
        return Container(
            alignment = Center,
            padding = 20.0,
            child = Column(
                children = listOfNotNull(
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Icon(
                            code = Theme.ICON_CHECK,
                            size = 80.0,
                            color = Theme.COLOR_SUCCESS,
                        ),
                    ),
                    Container(
                        alignment = TopCenter,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.onboard.success.title"),
                            alignment = TextAlignment.Center,
                            size = Theme.TEXT_SIZE_LARGE,
                            color = Theme.COLOR_PRIMARY,
                            bold = true,
                        ),
                    ),
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.onboard.success.sub-title"),
                            alignment = TextAlignment.Center,
                        ),
                    ),
                    Container(
                        padding = 20.0,
                    ),
                    Button(
                        id = "ok",
                        caption = getText("page.onboard.button.start"),
                        action = Action(
                            type = Command,
                            url = urlBuilder.build("/onboard/pages/success/start"),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/start")
    fun start(): Action =
        gotoLogin(
            phoneNumber = onboardDao.get().phoneNumber,
            auth = true,
            hideBackButton = true,
        )
}
