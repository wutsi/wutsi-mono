package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.membership.settings.security.dao.PasscodeRepository
import com.wutsi.application.membership.settings.security.dto.SubmitPasscodeRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode/pages/confirm")
class Passcode01ConfirmPage(
    private val dao: PasscodeRepository,
    private val securityManagerApi: SecurityManagerApi,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.passcode.confirm.title")

    override fun getSubTitle() = getText("page.settings.passcode.confirm.sub-title")

    override fun getBody(): WidgetAware =
        PinWithKeyboard(
            name = "pin",
            hideText = true,
            pinSize = 20.0,
            maxLength = 6,
            action = Action(
                type = Command,
                url = urlBuilder.build("/security/passcode/pages/confirm/submit"),
            ),
        )

    override fun getButton(): Button? = null

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPasscodeRequest): Action {
        val passcode = dao.get()
        return if (passcode.pin != request.pin) {
            promptError("prompt.error.pin-mismatch")
        } else {
            securityManagerApi.updatePassword(UpdatePasswordRequest(request.pin))
            return gotoNextPage()
        }
    }
}
