package com.wutsi.application.membership.settings.profile.screen

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.membership.settings.profile.dao.EmailRepository
import com.wutsi.application.membership.settings.profile.dto.SubmitOTPRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.error.ErrorURN
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/profile/email/verification")
class SettingsV2ProfileEmailVerificationScreen(
    private val mapper: ObjectMapper,
    private val dao: EmailRepository,
    private val securityManagerApi: SecurityManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        val email = dao.get()
        return Screen(
            id = Page.SETTINGS_PROFILE_EMAIL_VERIFICATION,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.email.verification.app-bar.title"),
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            alignment = TextAlignment.Center,
                            caption = getText(
                                "page.settings.profile.email.verification.title",
                                arrayOf(email.value),
                            ),
                            bold = true,
                            size = Theme.TEXT_SIZE_LARGE,
                            color = Theme.COLOR_PRIMARY,
                        ),
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            alignment = TextAlignment.Center,
                            caption = getText(
                                "page.settings.profile.email.verification.sub-title",
                                arrayOf(email.value),
                            ),
                        ),
                    ),
                    PinWithKeyboard(
                        id = "pin",
                        name = "code",
                        hideText = false,
                        pinSize = 40.0,
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("/settings/2/profile/email/verification/submit"),
                        ),
                    ),
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Button(
                            type = ButtonType.Text,
                            caption = getText("page.settings.profile.email.verification.resend"),
                            action = executeCommand(
                                url = urlBuilder.build("/settings/2/profile/email/verification/resend"),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(
        @RequestBody request: SubmitOTPRequest,
    ): Action {
        val email = dao.get()
        try {
            securityManagerApi.verifyOtp(
                token = email.token,
                request = VerifyOTPRequest(
                    code = request.code,
                ),
            )
            membershipManagerApi.updateMemberAttribute(
                request = UpdateMemberAttributeRequest(
                    name = "email",
                    value = email.value,
                ),
            )
            return gotoPreviousScreen()
        } catch (ex: FeignException) {
            val response = mapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
            return when (response.error.code) {
                ErrorURN.OTP_NOT_VALID.urn -> promptError("prompt.error.otp-mismatch")
                ErrorURN.OTP_EXPIRED.urn -> promptError("prompt.error.otp-expired")
                else -> throw ex
            }
        }
    }

    @PostMapping("/resend")
    fun resend() {
        val email = dao.get()
        email.token = securityManagerApi.createOtp(
            request = CreateOTPRequest(
                address = email.value,
                type = MessagingType.EMAIL.name,
            ),
        ).token
        dao.save(email)
    }
}
