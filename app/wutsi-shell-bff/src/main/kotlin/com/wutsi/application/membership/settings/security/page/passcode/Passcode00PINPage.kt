package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.membership.settings.security.dao.PasscodeRepository
import com.wutsi.application.membership.settings.security.dto.SubmitPasscodeRequest
import com.wutsi.application.membership.settings.security.entity.PasscodeEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType.Command
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode/pages/pin")
class Passcode00PINPage(
    private val dao: PasscodeRepository,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.passcode.pin.title")

    override fun getSubTitle(): String? = getText("page.settings.passcode.pin.sub-title")

    override fun getBody(): WidgetAware =
        PinWithKeyboard(
            name = "pin",
            hideText = true,
            pinSize = 20.0,
            maxLength = 6,
            action = Action(
                type = Command,
                url = urlBuilder.build("/security/passcode/pages/pin/submit"),
            ),
        )

    override fun getButton(): Button? = null

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPasscodeRequest): Action {
        dao.save(
            PasscodeEntity(
                pin = request.pin,
            ),
        )
        return gotoNextPage()
    }
}
