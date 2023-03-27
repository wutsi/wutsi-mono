package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.application.membership.onboard.dto.SubmitPinRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/pin")
class Onboard03PinPage : AbstractOnboardPage() {
    companion object {
        const val PAGE_INDEX = 3
    }

    @PostMapping
    fun index() = Column(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.start,
        children = listOfNotNull(
            Row(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOf(
                    IconButton(
                        icon = Theme.ICON_ARROW_BACK,
                        color = Theme.COLOR_BLACK,
                        action = gotoPage(PAGE_INDEX - 1),
                    ),
                ),
            ),
            Container(
                alignment = Center,
                padding = 20.0,
                child = Column(
                    children = listOf(
                        Container(
                            alignment = Center,
                            child = Text(
                                caption = getText("page.onboard.pin.title"),
                                alignment = TextAlignment.Center,
                                size = Theme.TEXT_SIZE_LARGE,
                                color = Theme.COLOR_PRIMARY,
                                bold = true,
                            ),
                        ),
                        Container(
                            alignment = TopCenter,
                            padding = 10.0,
                            child = Text(
                                caption = getText("page.onboard.pin.sub-title"),
                                alignment = TextAlignment.Center,
                            ),
                        ),
                        PinWithKeyboard(
                            id = "pin",
                            name = "pin",
                            hideText = true,
                            maxLength = 6,
                            pinSize = 20.0,
                            action = Action(
                                type = Command,
                                url = urlBuilder.build("/onboard/pages/pin/submit"),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    ).toWidget()

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPinRequest): Action {
        val data = onboardDao.get()
        data.pin = request.pin
        onboardDao.save(data)
        return gotoPage(PAGE_INDEX + 1)
    }
}
