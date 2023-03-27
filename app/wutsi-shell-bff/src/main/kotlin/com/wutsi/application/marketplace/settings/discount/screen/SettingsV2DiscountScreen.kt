package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListItemSwitch
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.Discount
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/2/discounts")
class SettingsV2DiscountScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(
        @RequestParam id: Long,
    ): Widget {
        val member = getCurrentMember()
        val discount = marketplaceManagerApi.getDiscount(id).discount

        return Screen(
            id = Page.SETTINGS_DISCOUNT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.discount.app-bar.title"),
            ),
            child = toInfoTab(discount, member),
        ).toWidget()
    }

    private fun toInfoTab(discount: Discount, member: Member): WidgetAware {
        val country = regulationEngine.country(member.country)
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormat, LocaleContextHolder.getLocale())

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOfNotNull(
                Flexible(
                    flex = 10,
                    child = ListView(
                        separatorColor = Theme.COLOR_DIVIDER,
                        separator = true,
                        children = listOfNotNull(
                            toListItemWidget(
                                "page.settings.discount.attribute.name",
                                discount.name,
                                urlBuilder.build("${Page.getSettingsDiscountEditorUrl()}?name=name&id=${discount.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.discount.attribute.rate",
                                "-${discount.rate}%",
                                urlBuilder.build("${Page.getSettingsDiscountEditorUrl()}?name=rate&id=${discount.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.discount.attribute.starts",
                                discount.starts?.let { DateTimeUtil.convert(it, member.timezoneId).format(dateFormat) },
                                urlBuilder.build("${Page.getSettingsDiscountEditorUrl()}?name=starts&id=${discount.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.discount.attribute.ends",
                                discount.ends?.let { DateTimeUtil.convert(it, member.timezoneId).format(dateFormat) },
                                urlBuilder.build("${Page.getSettingsDiscountEditorUrl()}?name=ends&id=${discount.id}"),
                            ),

                            ListItemSwitch(
                                name = "all-products",
                                selected = discount.allProducts,
                                caption = getText("page.settings.discount.attribute.all-products"),
                                subCaption = getText("page.settings.discount.attribute.all-products.description"),
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsDiscountUrl()}/apply-to"),
                                    parameters = mapOf(
                                        "id" to discount.id.toString(),
                                        "value" to (!discount.allProducts).toString(),
                                    ),
                                ),
                            ),

                            if (!discount.allProducts) {
                                ListItem(
                                    caption = getText("page.settings.discount.select-products"),
                                    subCaption = getText(
                                        "page.settings.discount.applied-to",
                                        arrayOf(discount.productIds.size),
                                    ),
                                    trailing = Icon(Theme.ICON_CHEVRON_RIGHT),
                                    action = gotoUrl(
                                        url = urlBuilder.build(Page.getSettingsDiscountProductUrl()),
                                        parameters = mapOf(
                                            "id" to discount.id.toString(),
                                        ),
                                    ),
                                )
                            } else {
                                null
                            },
                        ),
                    ),
                ),
            ),
        )
    }

    private fun toListItemWidget(caption: String, value: String?, url: String) = ListItem(
        caption = getText(caption),
        subCaption = value,
        trailing = Icon(
            code = Theme.ICON_EDIT,
            size = 24.0,
            color = Theme.COLOR_BLACK,
        ),
        action = Action(
            type = ActionType.Route,
            url = url,
        ),
    )

    @PostMapping("/apply-to")
    fun applyTo(@RequestParam id: Long, @RequestParam value: Boolean): Action {
        marketplaceManagerApi.updateDiscountAttribute(
            id,
            UpdateDiscountAttributeRequest(name = "all-products", value = value.toString()),
        )

        return gotoUrl(
            url = urlBuilder.build(Page.getSettingsDiscountUrl()),
            parameters = mapOf(
                "id" to id.toString(),
            ),
        )
    }
}
