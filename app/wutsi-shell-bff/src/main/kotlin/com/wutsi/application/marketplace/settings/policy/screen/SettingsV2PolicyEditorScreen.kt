package com.wutsi.application.marketplace.settings.policy.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.marketplace.service.PolicyEditorWidgetProvider
import com.wutsi.application.marketplace.settings.policy.dto.SubmitAttributeRequest
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
import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.membership.manager.MembershipManagerApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/policies/editor")
class SettingsV2PolicyEditorScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val widgetProvider: PolicyEditorWidgetProvider,
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam name: String): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val store = marketplaceManagerApi.getStore(member.storeId!!).store

        return Screen(
            id = Page.SETTINGS_POLICY_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.policy.attribute.$name"),
            ),
            child = Form(
                children = listOfNotNull(
                    Container(
                        alignment = Alignment.Center,
                        padding = 10.0,
                        child = Text(getText("page.settings.policy.attribute.$name.description")),
                    ),
                    Container(
                        padding = 20.0,
                    ),
                    Container(
                        padding = 10.0,
                        child = widgetProvider.get(name, store),
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.policy.editor.button.submit"),
                            action = executeCommand(
                                url = urlBuilder.build("${Page.getSettingsPoliciesEditorUrl()}/submit"),
                                parameters = mapOf(
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
        @RequestParam name: String,
        @RequestBody request: SubmitAttributeRequest,
    ): Action {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        marketplaceManagerApi.updateStorePolicyAttribute(
            id = member.storeId!!,
            request = UpdateStorePolicyAttributeRequest(
                name = name,
                value = request.value,
            ),
        )
        return gotoPreviousScreen()
    }
}
