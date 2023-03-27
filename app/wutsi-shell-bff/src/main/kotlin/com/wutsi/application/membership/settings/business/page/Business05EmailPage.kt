package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.dto.SubmitBusinessAttributeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/email")
class Business05EmailPage(
    private val securityManagerApi: SecurityManagerApi,
) : AbstractBusinessAttributePage() {
    companion object {
        const val PAGE_INDEX = 5
        const val ATTRIBUTE = "email"
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getAttribute(): String = ATTRIBUTE

    override fun getBody() = Container(
        padding = 10.0,
        child = widgetProvider.get(getAttribute(), dao.get().email, required = true),
    )

    override fun getButton() = Input(
        name = "value",
        type = InputType.Submit,
        caption = getText("page.settings.business.button.next"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsBusinessUrl()}/pages/email/submit"),
        ),
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitBusinessAttributeRequest): Action {
        val entity = dao.get()
        entity.email = request.value
        entity.otpToken = securityManagerApi.createOtp(
            request = CreateOTPRequest(
                address = request.value,
                type = MessagingType.EMAIL.name,
            ),
        ).token

        dao.save(entity)
        return gotoNextPage()
    }
}
