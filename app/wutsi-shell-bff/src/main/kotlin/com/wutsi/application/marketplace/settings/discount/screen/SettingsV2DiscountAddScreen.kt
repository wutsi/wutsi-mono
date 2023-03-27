package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.marketplace.settings.discount.dto.SubmitDiscountRequest
import com.wutsi.application.util.ChartDataType
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.CreateDiscountRequest
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/2/discounts/add")
class SettingsV2DiscountAddScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
) : AbstractEndpoint() {
    @PostMapping
    fun index(): Widget {
        return Screen(
            id = Page.SETTINGS_DISCOUNT_ADD,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.discount.add.app-bar.title"),
            ),
            child = SingleChildScrollView(
                child = Form(
                    children = listOfNotNull(
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "name",
                                maxLength = 30,
                                caption = getText("page.settings.discount.attribute.name"),
                                required = true,
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = DropdownButton(
                                name = "rate",
                                hint = getText("page.settings.discount.attribute.rate"),
                                children = listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70).map {
                                    DropdownMenuItem(
                                        value = it.toString(),
                                        caption = "-$it%",
                                    )
                                },
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "starts",
                                caption = getText("page.settings.discount.attribute.starts"),
                                type = InputType.Date,
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "ends",
                                caption = getText("page.settings.discount.attribute.ends"),
                                type = InputType.Date,
                            ),
                        ),
                        Container(padding = 20.0),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "submit",
                                caption = getText("page.settings.discount.add.button.submit"),
                                type = InputType.Submit,
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsDiscountAddUrl()}/submit"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitDiscountRequest): Action {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val zoneId = member.timezoneId?.let { ZoneId.of(it) } ?: ZoneId.of("UTC")
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", LocaleContextHolder.getLocale()).withZone(zoneId)

        val starts = LocalDate.parse(request.starts, fmt)
        val ends = LocalDate.parse(request.ends, fmt)

        val discountId = marketplaceManagerApi.createDiscount(
            request = CreateDiscountRequest(
                name = request.name,
                rate = request.rate,
                starts = starts.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                ends = ends.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                allProducts = false,
                type = ChartDataType.SALES.name,
            ),
        ).discountId

        return gotoUrl(
            url = urlBuilder.build(Page.getSettingsDiscountUrl()),
            parameters = mapOf("id" to discountId.toString()),
            replacement = true,
        )
    }
}
