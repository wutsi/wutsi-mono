package com.wutsi.application.membership.settings.profile.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.membership.settings.profile.dao.EmailRepository
import com.wutsi.application.membership.settings.profile.dto.SubmitProfileAttributeRequest
import com.wutsi.application.membership.settings.profile.entity.EmailEntity
import com.wutsi.application.membership.settings.service.ProfileEditorWidgetProvider
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
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/profile/editor")
class SettingsV2ProfileEditorScreen(
    private val editor: ProfileEditorWidgetProvider,
    private val dao: EmailRepository,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(@RequestParam name: String): Widget {
        val member = getCurrentMember()
        val attribute = if (name == "display-name" && member.business) "business-name" else name
        return Screen(
            id = Page.SETTINGS_PROFILE_EDITOR,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.profile.attribute.$attribute"),
            ),
            child = Form(
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Text(
                            getText("page.settings.profile.attribute.$attribute.description"),
                        ),
                    ),
                    Container(
                        padding = 20.0,
                    ),
                    Container(
                        padding = 10.0,
                        child = editor.get(name, member),
                    ),
                    Container(
                        padding = 10.0,
                        child = Input(
                            name = "submit",
                            type = InputType.Submit,
                            caption = getText("page.settings.profile.attribute.button.submit"),
                            action = executeCommand(
                                urlBuilder.build("${Page.getSettingsUrl()}/profile/editor/submit?name=$name"),
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
        @RequestBody request: SubmitProfileAttributeRequest,
    ): ResponseEntity<Action> {
        if (name == "email") {
            val member = getCurrentMember()
            if (member.email.equals(request.value, true)) {
                return ResponseEntity
                    .ok()
                    .body(gotoPreviousScreen())
            }

            val token = securityManagerApi.createOtp(
                request = CreateOTPRequest(
                    address = request.value,
                    type = MessagingType.EMAIL.name,
                ),
            ).token
            dao.save(
                EmailEntity(
                    value = request.value,
                    token = token,
                ),
            )

            return ResponseEntity
                .ok()
                .body(
                    gotoUrl(
                        url = urlBuilder.build("${Page.getSettingsUrl()}/profile/email/verification"),
                        replacement = true,
                    ),
                )
        } else {
            val headers = HttpHeaders()
            if (name == "language") {
                headers["x-language"] = request.value
            }

            membershipManagerApi.updateMemberAttribute(
                request = UpdateMemberAttributeRequest(
                    name = name,
                    value = request.value,
                ),
            )

            return ResponseEntity
                .ok()
                .headers(headers)
                .body(gotoPreviousScreen())
        }
    }
}
