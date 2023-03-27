package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.application.membership.onboard.dto.VerifyPhoneRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
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
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/verification")
class Onboard01VerificationPage(
    private val securityManagerApi: SecurityManagerApi,
) : AbstractOnboardPage() {
    companion object {
        const val PAGE_INDEX = 1
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
                Column(
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
                                caption = getText("page.onboard.verification.title"),
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
                                caption = getText(
                                    "page.onboard.verification.sub-title",
                                    arrayOf(formattedPhoneNumber(onboardDao.get().phoneNumber)),
                                ),
                                alignment = TextAlignment.Center,
                            ),
                        ),
                        Container(
                            alignment = Center,
                            child = Button(
                                caption = getText("page.onboard.verification.change-number"),
                                type = ButtonType.Text,
                                action = gotoPage(PAGE_INDEX - 1),
                            ),
                        ),
                        Form(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        id = "code",
                                        name = "code",
                                        type = InputType.Number,
                                        caption = getText("page.onboard.verification.field.code.caption"),
                                        required = true,
                                        hint = getText("page.onboard.verification.field.code.hint"),
                                        minLength = 6,
                                        maxLength = 6,
                                    ),
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        id = "submit",
                                        name = "submit",
                                        type = Submit,
                                        caption = getText("page.onboard.button.next"),
                                        action = Action(
                                            type = Command,
                                            url = urlBuilder.build("/onboard/pages/verification/submit"),
                                        ),
                                    ),
                                ),
                                Button(
                                    id = "resend-code",
                                    caption = getText("page.onboard.verification.resend-code"),
                                    type = ButtonType.Text,
                                    action = Action(
                                        type = Command,
                                        url = urlBuilder.build("/onboard/pages/verification/resend"),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/resend")
    fun resend() {
        val data = onboardDao.get()
        val response = securityManagerApi.createOtp(
            request = CreateOTPRequest(
                address = data.phoneNumber,
                type = MessagingType.SMS.name,
            ),
        )
        data.otpToken = response.token
        onboardDao.save(data)
    }

    @PostMapping("/submit")
    fun submit(@RequestBody request: VerifyPhoneRequest): Action {
        return try {
            val account = onboardDao.get()
            securityManagerApi.verifyOtp(
                token = account.otpToken,
                request = VerifyOTPRequest(
                    code = request.code,
                ),
            )
            gotoPage(PAGE_INDEX + 1)
        } catch (ex: Exception) {
            promptError("message.error.sms-verification-failed")
        }
    }
}
