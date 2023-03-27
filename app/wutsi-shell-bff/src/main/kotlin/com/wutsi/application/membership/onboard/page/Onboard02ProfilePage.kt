package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.application.membership.onboard.dto.SubmitProfileRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/profile")
class Onboard02ProfilePage : AbstractOnboardPage() {
    companion object {
        const val PAGE_INDEX = 2
    }

    @PostMapping
    fun index(): Widget {
        return Column(
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
                    alignment = TopCenter,
                    padding = 20.0,
                    child = Column(
                        children = listOf(
                            Container(
                                alignment = Center,
                                padding = 10.0,
                                child = Image(
                                    url = getLogoUrl(),
                                    width = 128.0,
                                    height = 128.0,
                                ),
                            ),
                            Container(
                                alignment = Center,
                                padding = 10.0,
                                child = Text(
                                    caption = getText("page.onboard.profile.title"),
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
                                    caption = getText("page.onboard.profile.sub-title"),
                                    alignment = TextAlignment.Center,
                                ),
                            ),
                            Form(
                                children = listOf(
                                    Container(
                                        padding = 10.0,
                                        child = Input(
                                            id = "display-name",
                                            name = "displayName",
                                            caption = getText("page.onboard.profile.field.display-name.caption"),
                                            required = true,
                                            minLength = 5,
                                            maxLength = 50,
                                            hint = getText("page.onboard.profile.field.display-name.hint"),
                                            value = onboardDao.get().displayName,
                                        ),
                                    ),
                                    Input(
                                        id = "submit",
                                        name = "submit",
                                        type = Submit,
                                        caption = getText("page.onboard.button.next"),
                                        action = Action(
                                            type = Command,
                                            url = urlBuilder.build("/onboard/pages/profile/submit"),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitProfileRequest): Action {
        val data = onboardDao.get()
        data.displayName = request.displayName
        onboardDao.save(data)
        return gotoPage(PAGE_INDEX + 1)
    }
}
