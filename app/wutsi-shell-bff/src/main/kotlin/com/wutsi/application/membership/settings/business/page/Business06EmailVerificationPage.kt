package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.Page
import com.wutsi.application.membership.settings.profile.dto.SubmitOTPRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/email/verification")
class Business06EmailVerificationPage(
    private val securityManagerApi: SecurityManagerApi,
) : AbstractBusinessAttributePage() {
    companion object {
        const val PAGE_INDEX = 6
        const val ATTRIBUTE = "email-verification"
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getAttribute(): String = ATTRIBUTE

    override fun getSubTitle() =
        getText("page.settings.profile.attribute.$ATTRIBUTE.description", arrayOf(dao.get().email))

    override fun getBody() = Column(
        children = listOf(
            Container(
                padding = 10.0,
                child = PinWithKeyboard(
                    id = "pin",
                    name = "code",
                    hideText = false,
                    pinSize = 40.0,
                    action = Action(
                        type = ActionType.Command,
                        url = urlBuilder.build("${Page.getSettingsBusinessUrl()}/pages/email/verification/submit"),
                    ),
                ),
            ),
            Container(
                padding = 10.0,
                alignment = Alignment.Center,
                child = Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.profile.email.verification.resend"),
                    action = executeCommand(
                        url = urlBuilder.build("${Page.getSettingsBusinessUrl()}/email/verification/resend"),
                    ),
                ),
            ),
        ),
    )

    override fun getButton(): WidgetAware? = null

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitOTPRequest): Action {
        val entity = dao.get()
        securityManagerApi.verifyOtp(
            token = entity.otpToken,
            request = VerifyOTPRequest(
                code = request.code,
            ),
        )
        return gotoNextPage()
    }

    @PostMapping("/resend")
    fun resend() {
        val entity = dao.get()
        entity.otpToken = securityManagerApi.createOtp(
            request = CreateOTPRequest(
                address = entity.email,
                type = MessagingType.EMAIL.name,
            ),
        ).token
        dao.save(entity)
    }
}
