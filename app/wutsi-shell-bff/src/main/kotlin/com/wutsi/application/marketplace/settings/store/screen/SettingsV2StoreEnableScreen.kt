package com.wutsi.application.marketplace.settings.store.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/store/activate")
class SettingsV2StoreEnableScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "${Page.getSettingsStoreActivateUrl()}/pages/start",
            "${Page.getSettingsStoreActivateUrl()}/pages/success",
        )
    }

    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_STORE_ENABLE,
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
