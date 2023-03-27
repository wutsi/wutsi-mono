package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.marketplace.settings.product.dto.SubmitProductEventRequest
import com.wutsi.application.util.DateTimeUtil
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
import com.wutsi.marketplace.manager.dto.UpdateProductEventRequest
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RestController
@RequestMapping("/settings/2/products/editor/event")
class SettingsV2ProductEditorEventScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val product = marketplaceManagerApi.getProduct(id).product
        val providers = marketplaceManagerApi.searchMeetingProvider().meetingProviders
            .map {
                DropdownMenuItem(
                    value = it.id.toString(),
                    caption = it.name,
                    icon = it.logoUrl,
                )
            }.toMutableList()
        providers.add(0, DropdownMenuItem(caption = "", value = ""))

        val locale = LocaleContextHolder.getLocale()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale)
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm", locale)
        val starts = product.event?.starts?.let {
            DateTimeUtil.convert(it, member.timezoneId)
        }
        val ends = product.event?.ends?.let {
            DateTimeUtil.convert(it, member.timezoneId)
        }

        return Screen(
            id = Page.SETTINGS_CATALOG_EDITOR_EVENT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.event.app-bar.title"),
            ),
            child = SingleChildScrollView(
                child = Form(
                    children = listOfNotNull(
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "startDate",
                                maxLength = 100,
                                caption = getText("page.settings.catalog.event.start-date"),
                                required = true,
                                type = InputType.Date,
                                value = starts?.format(dateFormat),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "startTime",
                                maxLength = 100,
                                caption = getText("page.settings.catalog.event.start-time"),
                                required = true,
                                type = InputType.Time,
                                value = starts?.format(timeFormat),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "endTime",
                                maxLength = 100,
                                caption = getText("page.settings.catalog.event.end-time"),
                                required = true,
                                type = InputType.Time,
                                value = ends?.format(timeFormat),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = DropdownButton(
                                name = "online",
                                hint = getText("page.settings.catalog.event.type"),
                                value = product.event?.online?.let { it.toString() } ?: "true",
                                children = listOf(
                                    DropdownMenuItem(
                                        caption = getText("page.settings.catalog.event.online"),
                                        value = "true",
                                    ),
                                    DropdownMenuItem(
                                        caption = getText("page.settings.catalog.event.offline"),
                                        value = "false",
                                    ),
                                ),
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = DropdownButton(
                                name = "meetingProviderId",
                                hint = getText("page.settings.catalog.event.provider"),
                                value = product.event?.meetingProvider?.let { it.id.toString() },
                                children = providers,
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "meetingId",
                                maxLength = 30,
                                caption = getText("page.settings.catalog.event.meeting-id"),
                                value = product.event?.meetingId,
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "meetingPassword",
                                maxLength = 100,
                                caption = getText("page.settings.catalog.event.meeting-password"),
                                value = product.event?.meetingPassword,
                            ),
                        ),
                        Container(padding = 20.0),
                        Container(
                            padding = 10.0,
                            child = Input(
                                name = "submit",
                                caption = getText("page.settings.catalog.event.button.submit"),
                                type = InputType.Submit,
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/event/submit"),
                                    parameters = mapOf(
                                        "id" to id.toString(),
                                    ),
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
        @RequestBody request: SubmitProductEventRequest,
    ): Action {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", LocaleContextHolder.getLocale())
        if (member.timezoneId != null) {
            fmt.timeZone = TimeZone.getTimeZone(member.timezoneId)
        }

        val starts = fmt.parse("${request.startDate} ${request.startTime}")
        val ends = fmt.parse("${request.startDate} ${request.endTime}")

        marketplaceManagerApi.updateProductEvent(
            request = UpdateProductEventRequest(
                productId = id,
                online = true,
                meetingId = request.meetingId,
                meetingPassword = request.meetingPassword,
                meetingProviderId = request.meetingProviderId,
                starts = starts.toInstant().atOffset(ZoneOffset.UTC),
                ends = ends.toInstant().atOffset(ZoneOffset.UTC),
            ),
        )

        return gotoPreviousScreen()
    }
}
