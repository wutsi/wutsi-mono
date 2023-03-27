package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractSuccessPageEndpoint
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.enums.ButtonType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/success")
class Business09SuccessPage : AbstractSuccessPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 9
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getTitle() = getText("page.settings.business.title")

    override fun getSubTitle() = getText("page.settings.business.congratulations")

    override fun getButton() = Column(
        children = listOf(
            Button(
                id = "ok",
                caption = getText("page.settings.business.button.done"),
                action = gotoUrl(
                    replacement = true,
                    url = urlBuilder.build(Page.getSettingsStoreActivateUrl()),
                ),
            ),
            Button(
                caption = getText("page.settings.business.button.not-now"),
                action = gotoPreviousScreen(),
                type = ButtonType.Text,
            ),
        ),
    )
}
