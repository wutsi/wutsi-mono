package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.Page
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/add")
class SettingsV2CatalogAddScreen : AbstractEndpoint() {
    companion object {
        private val PAGE_URLS = listOf(
            "${com.wutsi.application.Page.getSettingsProductAddUrl()}/pages/picture",
            "${com.wutsi.application.Page.getSettingsProductAddUrl()}/pages/type",
            "${com.wutsi.application.Page.getSettingsProductAddUrl()}/pages/editor",
        )
    }

    @PostMapping
    fun index() = Screen(
        id = com.wutsi.application.Page.SETTINGS_CATALOG_ADD,
        appBar = null,
        safe = true,
        child = PageView(
            children = PAGE_URLS.map {
                Page(url = urlBuilder.build(it))
            },
        ),
    ).toWidget()
}
