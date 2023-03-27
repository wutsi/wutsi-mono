package com.wutsi.application.checkout.settings.account.screen

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.Page
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/accounts/add/mobile")
class Settings2AccountAddMobileScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "settings/2/accounts/add/mobile/pages/phone",
            "settings/2/accounts/add/mobile/pages/verification",
            "settings/2/accounts/add/mobile/pages/success",
        )
    }

    @PostMapping
    fun index() = Screen(
        id = com.wutsi.application.Page.SETTINGS_ACCOUNT_ADD_MOBILE,
        safe = true,
        appBar = null,
        child = PageView(
            children = PAGE_URLS.map {
                Page(url = urlBuilder.build(it))
            },
        ),
    ).toWidget()
}
