package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.application.util.SecurityUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/start")
class Business00StartPage : AbstractBusinessPage() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.business.title")

    override fun getBody() = Container(
        padding = 20.0,
        margin = 20.0,
        border = 1.0,
        borderColor = Theme.COLOR_PRIMARY,
        background = Theme.COLOR_PRIMARY_LIGHT,
        child = Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                toRowWidget(Theme.ICON_STORE, "page.settings.business.why.store"),
                toRowWidget(Theme.ICON_WHATSAPP, "page.settings.business.why.chat"),
                toRowWidget(Theme.ICON_ORDER, "page.settings.business.why.order"),
                toRowWidget(Theme.ICON_MONEY, "page.settings.business.why.payment"),
            ),
        ),
    )

    override fun getButton() = Input(
        name = "value",
        type = InputType.Submit,
        caption = getText("page.settings.business.button.next"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsBusinessUrl()}/pages/start/submit"),
        ),
    )

    @PostMapping("/submit")
    fun submit(): Action {
        val member = membershipManagerApi.getMember(
            SecurityUtil.getMemberId(),
        ).member
        dao.save(
            BusinessEntity(
                displayName = member.displayName,
                cityId = member.city?.id,
                categoryId = member.category?.id,
                whatsapp = true,
                biography = member.biography,
                email = member.email ?: "",
            ),
        )
        return gotoPage(PAGE_INDEX + 1)
    }

    private fun toRowWidget(icon: String, text: String): WidgetAware =
        Row(
            children = listOf(
                Container(
                    padding = 5.0,
                    child = CircleAvatar(
                        child = Icon(
                            code = icon,
                            color = Theme.COLOR_PRIMARY,
                            size = 24.0,
                        ),
                        backgroundColor = Theme.COLOR_WHITE,
                        foregroundColor = Theme.COLOR_PRIMARY,
                        radius = 16.0,
                    ),
                ),
                Container(
                    padding = 5.0,
                    child = Text(getText(text)),
                ),
            ),
        )
}
