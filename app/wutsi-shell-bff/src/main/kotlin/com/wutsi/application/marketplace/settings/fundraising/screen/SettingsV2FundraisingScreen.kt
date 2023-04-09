package com.wutsi.application.marketplace.settings.fundraising.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
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
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/fundraising")
class SettingsV2FundraisingScreen(
    private val regulationEngine: RegulationEngine,
    private val marketplaceManagerApi: MarketplaceManagerApi,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val country = regulationEngine.country(member.country)
        val fmt = country.createMoneyFormat()
        val fundraising = marketplaceManagerApi.getFundraising(member.fundraisingId!!).fundraising

        return Screen(
            id = Page.SETTINGS_FUNDRAISING,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.fundraising.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.CenterLeft,
                        child = Text(
                            caption = getText("page.settings.fundraising.message"),
                            alignment = TextAlignment.Left,
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER, height = 2.0),
                    Flexible(
                        child = ListView(
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = listOfNotNull(
                                toListItemWidget(
                                    "page.settings.fundraising.attribute.amount",
                                    fmt.format(fundraising.amount),
                                    urlBuilder.build("${Page.getSettingsFundraisingEditorUrl()}?name=amount&id=${fundraising.id}"),
                                ),
                                toListItemWidget(
                                    "page.settings.fundraising.attribute.description",
                                    fundraising.description?.take(30),
                                    urlBuilder.build("${Page.getSettingsFundraisingEditorUrl()}?name=description&id=${fundraising.id}"),
                                ),
                                toListItemWidget(
                                    "page.settings.fundraising.attribute.video-url",
                                    fundraising.videoUrl,
                                    urlBuilder.build("${Page.getSettingsFundraisingEditorUrl()}?name=video-url&id=${fundraising.id}"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
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

}
