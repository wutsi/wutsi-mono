package com.wutsi.application.membership.settings.security.page.passcode

import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.flutter.sdui.Button
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/security/passcode/pages/success")
class Passcode02SuccessPage : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 2
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.passcode.success.title")

    override fun getSubTitle() = getText("page.settings.passcode.success.sub-title")

    override fun getButton() = Button(
        id = "ok",
        caption = getText("page.settings.passcode.button.done"),
        action = gotoPreviousScreen(),
    )
}
