package com.wutsi.application.membership.settings.about.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/about")
class AboutScreen(
    private val tracingContext: TracingContext,
    private val request: HttpServletRequest,
    private val environmentDetector: EnvironmentDetector,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = getCurrentMember()
        val os = request.getHeader("X-OS") ?: ""
        val osVersion = request.getHeader("X-OS-Version") ?: ""
        val osInfo = "$os $osVersion"
        val nextEnv = if (environmentDetector.prod()) "test" else "prod"

        return Screen(
            id = Page.ABOUT,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.about.app-bar.title"),
            ),
            child = Container(
                child = ListView(
                    separator = true,
                    separatorColor = Theme.COLOR_DIVIDER,
                    children = listOfNotNull(
                        Container(
                            padding = 10.0,
                            alignment = Alignment.Center,
                            child = Image(
                                url = getLogoUrl(),
                                width = 128.0,
                                height = 128.0,
                            ),
                        ),
                        listItem("page.settings.about.app-version", request.getHeader("X-Client-Version")),
                        listItem("page.settings.about.app-os", osInfo),
                        listItem("page.settings.about.device-id", tracingContext.deviceId()),
                        listItem("page.settings.about.user-id", member.id.toString()),
                        listItem("page.settings.about.environment", if (environmentDetector.prod()) "PROD" else "TEST"),

                        if (member.superUser) {
                            Container(
                                padding = 10.0,
                                child = Button(
                                    caption = getText(
                                        "page.settings.about.button.switch-environment",
                                        arrayOf(nextEnv.uppercase()),
                                    ),
                                    action = Action(
                                        type = ActionType.Command,
                                        url = urlBuilder.build("/about/switch-environment"),
                                        prompt = Dialog(
                                            type = DialogType.Confirm,
                                            title = getText("prompt.confirm.title"),
                                            message = getText(
                                                "page.settings.about.switch-environment-confirm",
                                                arrayOf(nextEnv.uppercase()),
                                            ),
                                        ).toWidget(),
                                        parameters = mapOf(
                                            "env" to nextEnv,
                                        ),
                                    ),
                                ),
                            )
                        } else {
                            null
                        },
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/switch-environment")
    fun switchEnvironment(@RequestParam env: String): ResponseEntity<Action> {
        // Logout
        securityManagerApi.logout()

        // Set the new environment and goto login
        val member = getCurrentMember()
        val headers = HttpHeaders()
        headers.add("x-environment", env)
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                gotoLogin(member.phoneNumber),
            )
    }

    private fun listItem(key: String, value: String?) = Row(
        children = listOf(
            Flexible(
                flex = 1,
                child = Container(
                    padding = 10.0,
                    child = Text(
                        getText(key),
                        bold = true,
                        alignment = TextAlignment.Right,
                        size = Theme.TEXT_SIZE_SMALL,
                    ),
                ),
            ),
            Flexible(
                flex = 3,
                child = Container(
                    padding = 10.0,
                    child = Text(value ?: "", alignment = TextAlignment.Left, size = Theme.TEXT_SIZE_SMALL),
                ),
            ),
        ),
    )
}
