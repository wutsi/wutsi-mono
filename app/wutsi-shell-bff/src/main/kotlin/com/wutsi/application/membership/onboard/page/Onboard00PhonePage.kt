package com.wutsi.application.membership.onboard.page

import com.wutsi.application.Theme
import com.wutsi.application.membership.onboard.dto.SubmitPhoneRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.application.service.CountryDetector
import com.wutsi.application.service.EnvironmentDetector
import com.wutsi.application.widget.EnvironmentBannerWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.InputType.Phone
import com.wutsi.flutter.sdui.enums.InputType.Submit
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/onboard/pages/phone")
class Onboard00PhonePage(
    private val env: EnvironmentDetector,
    private val countryDetector: CountryDetector,
    private val membershipManagerApi: MembershipManagerApi,
    private val securityManagerApi: SecurityManagerApi,
) : AbstractOnboardPage() {
    companion object {
        const val PAGE_INDEX = 0
    }

    @PostMapping
    fun index(): Widget {
        return Column(
            children = listOfNotNull(
                if (env.test()) {
                    EnvironmentBannerWidget()
                } else {
                    null
                },

                Container(
                    alignment = Center,
                    padding = 10.0,
                    child = Image(
                        url = getLogoUrl(),
                        width = 128.0,
                        height = 128.0,
                    ),
                ),
                Container(
                    alignment = Center,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.onboard.phone.title"),
                        alignment = TextAlignment.Center,
                        size = Theme.TEXT_SIZE_LARGE,
                        color = Theme.COLOR_PRIMARY,
                        bold = true,
                    ),
                ),
                Container(
                    alignment = TopCenter,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.onboard.phone.sub-title"),
                        alignment = TextAlignment.Center,
                    ),
                ),
                Form(
                    children = listOf(
                        Container(
                            padding = 10.0,
                            child = Input(
                                id = "phone-number",
                                name = "phoneNumber",
                                type = Phone,
                                caption = getText("page.onboard.phone.field.phone.caption"),
                                required = true,
                                initialCountry = "CM",
                            ),
                        ),
                        Container(
                            padding = 10.0,
                            child = Input(
                                id = "submit",
                                name = "submit",
                                type = Submit,
                                caption = getText("page.onboard.button.next"),
                                action = Action(
                                    type = Command,
                                    url = urlBuilder.build("/onboard/pages/phone/submit"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPhoneRequest): Action {
        val members = membershipManagerApi.searchMember(
            request = SearchMemberRequest(
                phoneNumber = request.phoneNumber,
                limit = 1,
            ),
        ).members

        return if (members.isEmpty()) {
            val token = securityManagerApi.createOtp(
                request = CreateOTPRequest(
                    address = request.phoneNumber,
                    type = MessagingType.SMS.name,
                ),
            ).token
            onboardDao.save(
                OnboardEntity(
                    phoneNumber = request.phoneNumber,
                    otpToken = token,
                    country = countryDetector.detect(request.phoneNumber),
                    language = LocaleContextHolder.getLocale().language,
                ),
            )
            gotoPage(PAGE_INDEX + 1)
        } else {
            gotoLogin(
                phoneNumber = request.phoneNumber,
                title = getText("page.onboard.phone.login.title"),
            )
        }
    }
}
