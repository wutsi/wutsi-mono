package com.wutsi.application.marketplace.settings.fundraising.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/fundraising/activate")
class SettingsV2FundraisingEnableScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "${Page.getSettingsFundraisingActivateUrl()}/pages/start",
            "${Page.getSettingsFundraisingActivateUrl()}/pages/success",
        )
    }

    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_FUNDRAISING_ENABLE,
            safe = true,
            appBar = null,
            child = PageView(
                children = PAGE_URLS.map {
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build(it))
                },
            ),
        ).toWidget()
    }
}
