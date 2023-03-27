package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.PhotoView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/pictures")
class SettingsV2ProductPictureViewerScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
) : AbstractEndpoint() {
    @PostMapping
    fun index(
        @RequestParam id: Long,
        @RequestParam url: String,
    ): Widget {
        return Screen(
            id = Page.SETTINGS_CATALOG_PICTURE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.picture.app-bar.title"),
                automaticallyImplyLeading = false,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_CANCEL,
                        color = Theme.COLOR_BLACK,
                        action = gotoPreviousScreen(),
                    ),
                ),
            ),
            child = PhotoView(url = url),
            floatingActionButton = Button(
                type = ButtonType.Floatable,
                icon = Theme.ICON_DELETE,
                stretched = false,
                color = Theme.COLOR_WHITE,
                action = executeCommand(
                    url = urlBuilder.build("${Page.getSettingsProductPictureUrl()}/delete?id=$id"),
                    confirm = getText("page.settings.catalog.picture.confirm-delete"),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/delete")
    fun delete(@RequestParam id: Long): Action {
        marketplaceManagerApi.deletePicture(id)
        return gotoPreviousScreen()
    }
}
