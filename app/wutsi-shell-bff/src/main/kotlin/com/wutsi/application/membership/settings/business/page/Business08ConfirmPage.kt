package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.Page
import com.wutsi.checkout.manager.dto.CreateBusinessRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/business/pages/confirm")
class Business08ConfirmPage : AbstractBusinessPage() {
    companion object {
        const val PAGE_INDEX = 8
    }

    override fun getPageIndex(): Int = PAGE_INDEX

    override fun getTitle() = getText("page.settings.business.title")

    override fun getSubTitle() = getText("page.settings.business.confirm")

    override fun getBody(): WidgetAware =
        Column(
            children = listOf(
                Container(
                    padding = 10.0,
                    child = Button(
                        caption = getText("page.settings.business.button.yes"),
                        action = Action(
                            type = ActionType.Command,
                            url = urlBuilder.build("${Page.getSettingsBusinessUrl()}/pages/confirm/submit"),
                        ),
                    ),
                ),
                Container(
                    padding = 10.0,
                    child = Button(
                        type = ButtonType.Text,
                        caption = getText("page.settings.business.button.no"),
                        action = gotoPreviousScreen(),
                    ),
                ),
            ),
        )

    override fun getButton(): WidgetAware? = null

    @PostMapping("/submit")
    fun submit(): Action {
        val entity = dao.get()

        checkoutManagerApi.createBusiness(
            request = CreateBusinessRequest(
                displayName = entity.displayName,
                cityId = entity.cityId ?: -1,
                categoryId = entity.categoryId ?: -1,
                whatsapp = entity.whatsapp,
                biography = entity.biography,
                email = entity.email,
            ),
        )
        return gotoNextPage()
    }
}
