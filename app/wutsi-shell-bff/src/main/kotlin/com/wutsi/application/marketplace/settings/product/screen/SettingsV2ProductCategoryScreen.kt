package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.PageView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/category")
class SettingsV2ProductCategoryScreen : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val urls = listOf(
            "${Page.getSettingsProductCategoryUrl()}/pages/level-0?id=$id",
            "${Page.getSettingsProductCategoryUrl()}/pages/level-1?id=$id",
            "${Page.getSettingsProductCategoryUrl()}/pages/level-2?id=$id",
        )

        return Screen(
            id = Page.SETTINGS_CATALOG_CATEGORY,
            safe = true,
            appBar = null,
            child = PageView(
                children = urls.map {
                    com.wutsi.flutter.sdui.Page(url = urlBuilder.build(it))
                },
            ),
        ).toWidget()
    }
}
