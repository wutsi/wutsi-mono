package com.wutsi.application.checkout.settings.account.page

import com.wutsi.application.checkout.settings.account.dao.AccountRepository
import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.application.util.PhoneUtil
import com.wutsi.flutter.sdui.Button
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/accounts/add/mobile/pages/success")
class AddMobile02SuccessPage(
    private val dao: AccountRepository,
) : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 2
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.account.add.mobile.success.title")

    override fun getSubTitle() =
        getText("page.settings.account.add.mobile.success.sub-title", arrayOf(PhoneUtil.format(dao.get().number)))

    override fun getButton() = Button(
        caption = getText("page.settings.account.add.mobile.button.done"),
        action = gotoPreviousScreen(),
    )
}
