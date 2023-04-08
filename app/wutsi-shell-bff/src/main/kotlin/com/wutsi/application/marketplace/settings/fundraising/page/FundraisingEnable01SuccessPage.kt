package com.wutsi.application.marketplace.settings.fundraising.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.flutter.sdui.Button
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/fundraising/activate/pages/success")
class FundraisingEnable01SuccessPage : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getBaseId() = Page.SETTINGS_FUNDRAISING_ENABLE

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getTitle() = getText("page.settings.fundraising.activate.success.title")

    override fun getSubTitle() = getText("page.settings.fundraising.activate.success.sub-title")

    override fun getButton() = Button(
        caption = getText("page.settings.fundraising.activate.button.done"),
        action = gotoUrl(
            url = urlBuilder.build(Page.getSettingsFundraisingUrl()),
            replacement = true,
        ),
    )
}
