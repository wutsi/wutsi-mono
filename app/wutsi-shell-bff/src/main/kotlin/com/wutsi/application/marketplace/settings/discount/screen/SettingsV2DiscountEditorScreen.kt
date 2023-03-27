package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.marketplace.service.DiscountEditorWidgetProvider
import com.wutsi.application.marketplace.settings.product.dto.SubmitAttributeRequest
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.UpdateDiscountAttributeRequest
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/2/discounts/editor")
class SettingsV2DiscountEditorScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val widgetProvider: DiscountEditorWidgetProvider,
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long, @RequestParam name: String): Widget {
        val discount = marketplaceManagerApi.getDiscount(id).discount
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member

        return Screen(
            id = Page.SETTINGS_DISCOUNT_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.discount.attribute.$name"),
            ),
            child = Form(
                children = listOfNotNull(
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(getText("page.settings.discount.attribute.$name.description")),
                    ),
                    Container(
                        padding = 20.0,
                    ),
                    Container(
                        padding = 10.0,
                        child = widgetProvider.get(name, discount, member.timezoneId),
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.discount.button.submit"),
                            action = executeCommand(
                                url = urlBuilder.build("${Page.getSettingsDiscountEditorUrl()}/submit"),
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

    @PostMapping("/submit")
    fun submit(
        @RequestParam id: Long,
        @RequestParam name: String,
        @RequestBody request: SubmitAttributeRequest,
    ): Action {
        marketplaceManagerApi.updateDiscountAttribute(
            id = id,
            request = UpdateDiscountAttributeRequest(
                name = name,
                value = if (name == "starts" || name == "ends") {
                    formatDate(request.value)
                } else {
                    request.value
                },
            ),
        )

        return gotoPreviousScreen()
    }

    private fun formatDate(value: String?): String? {
        if (value.isNullOrEmpty()) {
            return null
        }

        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val zoneId = member.timezoneId?.let { ZoneId.of(it) } ?: ZoneId.of("UTC")
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", LocaleContextHolder.getLocale()).withZone(zoneId)

        val date = LocalDate.parse(value, fmt).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00", LocaleContextHolder.getLocale())
            .format(date)
    }
}
