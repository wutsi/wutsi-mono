package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.SearchProductRequest
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/discounts/products")
class SettingsV2DiscountProductScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val member = getCurrentMember()
        val country = regulationEngine.country(member.country)
        val fmt = country.createMoneyFormat()
        val discount = marketplaceManagerApi.getDiscount(id).discount
        val products = marketplaceManagerApi.searchProduct(
            request = SearchProductRequest(
                storeId = member.storeId,
                limit = regulationEngine.maxProducts(),
            ),
        ).products

        return Screen(
            id = Page.SETTINGS_DISCOUNT_PRODUCTS,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = discount.name,
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(getText("page.settings.discounts.products.title")),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                    Flexible(
                        child = ListView(
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = products
                                .filter { it.thumbnailUrl != null }
                                .map {
                                    ListItemSwitch(
                                        name = "value",
                                        caption = it.title,
                                        subCaption = it.price?.let { fmt.format(it) },
                                        selected = discount.productIds.contains(it.id),
                                        icon = it.thumbnailUrl?.let {
                                            imageService.transform(
                                                it,
                                                Transformation(
                                                    dimension = Dimension(width = 48, height = 48),
                                                ),
                                            )
                                        },
                                        action = executeCommand(
                                            urlBuilder.build("${Page.getSettingsDiscountProductUrl()}/toggle"),
                                            parameters = mapOf(
                                                "discount-id" to id.toString(),
                                                "product-id" to it.id.toString(),
                                                "value" to discount.productIds.contains(it.id).toString(),
                                            ),
                                        ),
                                    )
                                },
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/toggle")
    fun toggle(
        @RequestParam(name = "discount-id") discountId: Long,
        @RequestParam(name = "product-id") productId: Long,
        @RequestParam value: Boolean,
    ): Action? {
        if (value) {
            marketplaceManagerApi.removeDiscountProduct(discountId, productId)
        } else {
            marketplaceManagerApi.addDiscountProduct(discountId, productId)
        }
        return null
    }
}
