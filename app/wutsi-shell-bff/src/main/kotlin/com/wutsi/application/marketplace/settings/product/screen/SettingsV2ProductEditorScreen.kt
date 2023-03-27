package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.marketplace.service.ProductEditorWidgetProvider
import com.wutsi.application.marketplace.settings.product.dto.SubmitAttributeRequest
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.ProductAttribute
import com.wutsi.marketplace.manager.dto.UpdateProductAttributeListRequest
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/editor")
class SettingsV2ProductEditorScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val widgetProvider: ProductEditorWidgetProvider,
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long, @RequestParam name: String): Widget {
        val product = marketplaceManagerApi.getProduct(id).product
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        return Screen(
            id = Page.SETTINGS_CATALOG_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.product.attribute.$name"),
            ),
            child = Form(
                children = listOfNotNull(
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(getText("page.settings.catalog.product.attribute.$name.description")),
                    ),
                    Container(
                        padding = 20.0,
                    ),
                    Container(
                        padding = 10.0,
                        child = Column(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.start,
                            children = listOfNotNull(
                                widgetProvider.get(name, product, member.country),
                                Container(padding = 5.0),
                                getHint(name)?.let {
                                    Container(child = Text(it))
                                },
                            ),
                        ),
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.store.product.button.submit"),
                            action = executeCommand(
                                url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/submit"),
                                parameters = mapOf(
                                    "id" to id.toString(),
                                    "name" to name,
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun getHint(name: String): String? =
        try {
            getText("page.settings.catalog.product.attribute.$name.hint")
        } catch (ex: Exception) {
            null
        }

    @PostMapping("/submit")
    fun submit(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestBody request: SubmitAttributeRequest,
    ): Action {
        marketplaceManagerApi.updateProductAttribute(
            request = UpdateProductAttributeListRequest(
                productId = id,
                attributes = listOf(
                    ProductAttribute(
                        name = name,
                        value = request.value,
                    ),
                ),
            ),
        )

        return gotoPreviousScreen()
    }
}
