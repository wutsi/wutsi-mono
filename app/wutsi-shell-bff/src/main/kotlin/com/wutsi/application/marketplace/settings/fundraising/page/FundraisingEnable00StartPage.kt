package com.wutsi.application.marketplace.settings.fundraising.page

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.regulation.RegulationEngine
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/fundraising/activate/pages/start")
class FundraisingEnable00StartPage(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getBaseId() = Page.SETTINGS_FUNDRAISING_ENABLE

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.fundraising.activate.start.title")

    override fun getSubTitle() = getText("page.settings.fundraising.activate.start.sub-title")

    override fun getBody(): WidgetAware {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val country = regulationEngine.country(member.country)
        val fmt = country.createMoneyFormat()
        return Column(
            children = listOf(
                toRowWidget(getText("page.settings.fundraising.activate.start.setup-fees"), fmt.format(0.0)),
                toRowWidget(getText("page.settings.fundraising.activate.start.monthly-fees"), fmt.format(0.0)),
                toRowWidget(
                    getText("page.settings.fundraising.activate.start.transaction-fees"),
                    Column(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.start,
                        children = listOf(
                            toValueWidget("10%"),
                            Text(
                                caption = getText("page.settings.fundraising.activate.start.transaction-fees-hint"),
                                size = Theme.TEXT_SIZE_SMALL,
                            ),
                        ),
                    ),
                ),
                Container(padding = 10.0),
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.settings.fundraising.activate.button.yes"),
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("${Page.getSettingsFundraisingActivateUrl()}/pages/start/submit"),
                        ),
                    ),
                ),
                Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.fundraising.activate.button.no"),
                    action = gotoPreviousScreen(),
                ),
            ),
        )
    }

    override fun getButton(): WidgetAware? = null

    @PostMapping("/submit")
    fun submit(): Action {
        marketplaceManagerApi.createFundraising()
        return gotoPage(PAGE_INDEX + 1)
    }

    private fun toRowWidget(name: String, value: String): WidgetAware =
        toRowWidget(
            name,
            toValueWidget(value),
        )

    private fun toValueWidget(value: String): WidgetAware =
        Text(value, bold = true, color = Theme.COLOR_PRIMARY, size = Theme.TEXT_SIZE_X_LARGE)

    private fun toRowWidget(name: String, value: WidgetAware): WidgetAware =
        Row(
            mainAxisAlignment = MainAxisAlignment.center,
            crossAxisAlignment = CrossAxisAlignment.center,
            children = listOf(
                Flexible(
                    child = Container(
                        padding = 10.0,
                        child = Text(name, alignment = TextAlignment.Right),
                    ),
                ),
                Flexible(
                    child = Container(
                        padding = 10.0,
                        child = value,
                    ),
                ),
            ),
        )
}
