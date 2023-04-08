package com.wutsi.application.membership.login.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.membership.login.dto.SubmitPasscodeRequest
import com.wutsi.application.membership.onboard.screen.OnboardV2Screen
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.application.util.PhoneUtil
import com.wutsi.application.util.StringUtil.initials
import com.wutsi.application.widget.EnvironmentBannerWidget
import com.wutsi.enums.LoginType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.CircleAvatar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.MemberSummary
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.net.URLEncoder
import javax.validation.Valid

@RestController
@RequestMapping("/login/2")
class LoginV2Screen(
    private val membershipManagerApi: MembershipManagerApi,
    private val securityManagerApi: SecurityManagerApi,
    private val onboardScreen: OnboardV2Screen,
    private val env: EnvironmentDetector,

    @Value("\${wutsi.feature-flags.switch-account}") private val switchAccountEnabled: Boolean,
) : AbstractEndpoint() {
    @PostMapping()
    fun index(
        @RequestParam(name = "phone") phoneNumber: String,
        @RequestParam(name = "screen-id", required = false) screenId: String? = null,
        @RequestParam(name = "icon", required = false) icon: String? = null,
        @RequestParam(name = "title", required = false) title: String? = null,
        @RequestParam(name = "sub-title", required = false) subTitle: String? = null,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "return-to-route", required = false, defaultValue = "true") returnToRoute: Boolean = true,
        @RequestParam(name = "auth", required = false, defaultValue = "true") auth: Boolean = true,
        @RequestParam(name = "dark-mode", required = false, defaultValue = "false") darkMode: Boolean = false,
        @RequestParam(name = "hide-back-button", required = false) hideBackButton: Boolean? = null,
        @RequestParam(
            name = "hide-change-account-button",
            required = false,
            defaultValue = "false",
        ) hideChangeAccountButton: Boolean = false,
    ): Widget {
        // Find member
        val member = findMember(phoneNumber)
            ?: return onboardScreen.index()

        // Widget
        val textColor = textColor(darkMode)
        val backgroundColor = backgroundColor(darkMode)
        logger.add("member_id", member.id)
        return Screen(
            id = screenId ?: Page.LOGIN,
            appBar = AppBar(
                backgroundColor = backgroundColor,
                foregroundColor = textColor,
                elevation = 0.0,
                title = title,
                automaticallyImplyLeading = false,
                actions = if (hideBackButton == false) {
                    listOf(
                        IconButton(
                            icon = Theme.ICON_CANCEL,
                            color = Theme.COLOR_BLACK,
                            action = gotoPreviousScreen(),
                        ),
                    )
                } else {
                    null
                },
            ),
            backgroundColor = backgroundColor,
            child = SingleChildScrollView(
                child = Container(
                    alignment = Center,
                    child = Column(
                        children = listOfNotNull(
                            if (env.test()) {
                                EnvironmentBannerWidget()
                            } else {
                                null
                            },

                            Container(
                                padding = 10.0,
                                alignment = Center,
                                child = CircleAvatar(
                                    radius = 16.0,
                                    child = if (member.pictureUrl.isNullOrEmpty()) {
                                        Text(
                                            caption = initials(member.displayName),
                                            color = textColor,
                                        )
                                    } else {
                                        Image(
                                            url = member.pictureUrl!!,
                                        )
                                    },
                                ),
                            ),
                            Container(
                                padding = 10.0,
                                alignment = Center,
                                child = Text(
                                    caption = subTitle ?: getText("page.login.sub-title"),
                                    color = textColor,
                                    alignment = TextAlignment.Center,
                                    size = Theme.TEXT_SIZE_X_LARGE,
                                ),
                            ),
                            Container(
                                alignment = Center,
                                child = PinWithKeyboard(
                                    id = "pin",
                                    name = "pin",
                                    hideText = true,
                                    maxLength = 6,
                                    keyboardButtonSize = 70.0,
                                    pinSize = 20.0,
                                    action = Action(
                                        type = Command,
                                        url = urlBuilder.build(
                                            submitUrl(
                                                phoneNumber,
                                                auth,
                                                returnUrl,
                                                returnToRoute,
                                            ),
                                        ),
                                    ),
                                    color = textColor,
                                ),
                            ),
                            if (auth && !hideChangeAccountButton && switchAccountEnabled) {
                                Container(
                                    padding = 10.0,
                                    child = Button(
                                        id = "other-account",
                                        type = ButtonType.Text,
                                        caption = getText("page.login.button.another-account"),
                                        action = gotoUrl(
                                            url = urlBuilder.build(Page.getOnboardUrl()),
                                            type = ActionType.Route,
                                        ),
                                    ),
                                )
                            } else {
                                null
                            },
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(
        @RequestParam(name = "phone") phoneNumber: String,
        @RequestParam(name = "auth", required = false, defaultValue = "true") auth: Boolean = true,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "return-to-route", required = false, defaultValue = "true") returnToRoute: Boolean = true,
        @Valid @RequestBody
        request: SubmitPasscodeRequest,
    ): ResponseEntity<Action> {
        try {
            val headers = HttpHeaders()
            if (auth) {
                val accessToken = authenticate(phoneNumber, request)
                headers["x-access-token"] = accessToken
            } else {
                validate(request)
            }

            val action = returnUrl
                ?.let {
                    gotoUrl(
                        url = URLDecoder.decode(it, "utf-8"),
                        type = actionType(returnToRoute),
                        replacement = if (returnToRoute) true else null,
                    )
                }
                ?: gotoUrl(
                    url = urlBuilder.build(Page.getHomeUrl()),
                    type = ActionType.Route,
                    replacement = true,
                )

            logger.add("action_url", action.url)
            logger.add("action_type", action.type)
            logger.add("action_replacement", action.replacement)
            return ResponseEntity
                .ok()
                .headers(headers)
                .body(action)
        } catch (ex: Exception) {
            return ResponseEntity
                .ok()
                .body(promptError("message.error.login-failed"))
        }
    }

    private fun authenticate(phoneNumber: String, request: SubmitPasscodeRequest): String =
        securityManagerApi.login(
            request = com.wutsi.security.manager.dto.LoginRequest(
                type = LoginType.PASSWORD.name,
                username = PhoneUtil.sanitize(phoneNumber),
                password = request.pin,
            ),
        ).accessToken

    private fun validate(request: SubmitPasscodeRequest) =
        securityManagerApi.verifyPassword(
            request = VerifyPasswordRequest(
                value = request.pin,
            ),
        )

    private fun actionType(returnToRoute: Boolean): ActionType =
        if (returnToRoute) ActionType.Route else ActionType.Command

    private fun findMember(phoneNumber: String): MemberSummary? {
        val members = membershipManagerApi.searchMember(
            request = SearchMemberRequest(
                phoneNumber = PhoneUtil.sanitize(phoneNumber),
                limit = 1,
            ),
        ).members
        return if (members.isEmpty()) {
            null
        } else {
            members[0]
        }
    }

    private fun textColor(darkMode: Boolean): String =
        if (darkMode) Theme.COLOR_WHITE else Theme.COLOR_BLACK

    private fun backgroundColor(darkMode: Boolean): String =
        if (darkMode) Theme.COLOR_PRIMARY else Theme.COLOR_WHITE

    private fun submitUrl(phoneNumber: String, auth: Boolean, returnUrl: String?, returnToRoute: Boolean): String {
        val url =
            Page.getLoginUrl() + "/submit?auth=$auth&return-to-route=$returnToRoute&phone=" + URLEncoder.encode(
                phoneNumber,
                "utf-8",
            )
        return if (returnUrl == null) {
            url
        } else {
            url + "&return-url=" + URLEncoder.encode(returnUrl, "utf-8")
        }
    }
}
