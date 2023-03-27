package com.wutsi.application.marketplace.settings.discount.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.DiscountSummary
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.membership.manager.dto.Member
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/settings/2/discounts/list")
class SettingsV2DiscountListScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val discounts = marketplaceManagerApi.searchDiscount(
            request = SearchDiscountRequest(
                storeId = member.storeId ?: -1,
                limit = 100,
            ),
        ).discounts
        val country = regulationEngine.country(member.country)

        return Screen(
            id = Page.SETTINGS_DISCOUNT_LIST,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.discounts.app-bar.title"),
            ),
            floatingActionButton = Button(
                type = ButtonType.Floatable,
                icon = Theme.ICON_ADD,
                stretched = false,
                iconColor = Theme.COLOR_WHITE,
                action = gotoUrl(
                    url = urlBuilder.build(Page.getSettingsDiscountAddUrl()),
                ),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = if (discounts.isEmpty()) {
                                getText("page.settings.discounts.0_count")
                            } else if (discounts.size == 1) {
                                getText("page.settings.discounts.1_count")
                            } else {
                                getText("page.settings.discounts.n_count", arrayOf(discounts.size))
                            },
                            alignment = TextAlignment.Center,
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                    Flexible(
                        child = toListViewWidget(discounts, country, member),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toListViewWidget(discounts: List<DiscountSummary>, country: Country, member: Member): ListView {
        val children = mutableListOf<WidgetAware>()
        val now = OffsetDateTime.now(ZoneId.of("UTC"))

        addListItems(
            caption = getText("page.settings.discounts.current"),
            discounts = discounts.filter { isCurrent(it, now) },
            country = country,
            member = member,
            children = children,
        )
        addListItems(
            caption = getText("page.settings.discounts.upcoming"),
            discounts = discounts.filter { isUpcoming(it, now) },
            country = country,
            member = member,
            children = children,
        )
        addListItems(
            caption = getText("page.settings.discounts.past"),
            discounts = discounts.filter { isPast(it, now) },
            country = country,
            member = member,
            children = children,
        )

        return ListView(
            separator = true,
            separatorColor = Theme.COLOR_DIVIDER,
            children = children,
        )
    }

    private fun isCurrent(discount: DiscountSummary, now: OffsetDateTime): Boolean =
        (discount.starts != null && discount.starts!!.isBefore(now)) &&
            (discount.ends == null || discount.ends!!.isAfter(now))

    private fun isUpcoming(discount: DiscountSummary, now: OffsetDateTime): Boolean =
        (discount.starts != null && discount.starts!!.isAfter(now))

    private fun isPast(discount: DiscountSummary, now: OffsetDateTime): Boolean =
        (discount.ends != null && discount.ends!!.isBefore(now))

    private fun addListItems(
        caption: String,
        discounts: List<DiscountSummary>,
        country: Country,
        member: Member,
        children: MutableList<WidgetAware>,
    ) {
        if (discounts.isNotEmpty()) {
            children.add(
                Container(
                    alignment = Alignment.CenterLeft,
                    background = Theme.COLOR_GRAY_LIGHT,
                    padding = 10.0,
                    width = Double.MAX_VALUE,
                    child = Text(
                        caption = caption,
                        bold = true,
                        size = Theme.TEXT_SIZE_LARGE,
                    ),
                ),
            )
            children.addAll(
                discounts.map { toListItemWidget(it, country, member) },
            )
        }
    }

    private fun toListItemWidget(discount: DiscountSummary, country: Country, member: Member) = ListItem(
        caption = discount.name,
        trailing = Icon(code = Theme.ICON_CHEVRON_RIGHT),
        subCaption = toDuration(discount, country, member),
        action = gotoUrl(
            url = urlBuilder.build(Page.getSettingsDiscountUrl()),
            parameters = mapOf("id" to discount.id.toString()),
        ),
    )

    private fun toDuration(discount: DiscountSummary, country: Country, member: Member): String? {
        val locale = LocaleContextHolder.getLocale()
        return if (discount.starts != null && discount.ends != null) {
            DateTimeUtil.convert(discount.starts!!, member.timezoneId)
                .format(DateTimeFormatter.ofPattern(country.dateFormatShort, locale)) +
                " - " +
                DateTimeUtil.convert(discount.ends!!, member.timezoneId)
                    .format(DateTimeFormatter.ofPattern(country.dateFormat, locale))
        } else if (discount.starts != null) {
            DateTimeUtil.convert(discount.starts!!, member.timezoneId)
                .format(DateTimeFormatter.ofPattern(country.dateFormat, locale))
        } else {
            null
        }
    }
}
