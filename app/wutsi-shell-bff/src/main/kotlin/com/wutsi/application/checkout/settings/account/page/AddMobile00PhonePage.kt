package com.wutsi.application.checkout.settings.account.page

import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.dao.AccountRepository
import com.wutsi.application.checkout.settings.account.entity.AccountEntity
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.membership.onboard.dto.SubmitPhoneRequest
import com.wutsi.application.util.SecurityUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.SearchPaymentProviderRequest
import com.wutsi.enums.PaymentMethodType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/accounts/add/mobile/pages/phone")
class AddMobile00PhonePage(
    private val dao: AccountRepository,
    private val checkoutManagerApi: CheckoutManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val securityManager: SecurityManagerApi,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.account.add.mobile.phone.title")

    override fun getSubTitle(): String? = getText("page.settings.account.add.mobile.phone.sub-title")

    override fun getBody(): WidgetAware {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val providers = checkoutManagerApi.searchPaymentProvider(
            request = SearchPaymentProviderRequest(
                type = PaymentMethodType.MOBILE_MONEY.name,
                country = member.country,
            ),
        ).paymentProviders

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOf(
                Container(
                    padding = 10.0,
                    child = Input(
                        name = "phoneNumber",
                        type = InputType.Phone,
                        required = true,
                        initialCountry = member.country,
                    ),
                ),
                Container(
                    padding = 10.0,
                    child = Row(
                        children = providers.map {
                            Image(
                                width = 32.0,
                                height = 32.0,
                                url = it.logoUrl,
                            )
                        },
                    ),
                ),
            ),
        )
    }

    override fun getButton() = Input(
        name = "submit",
        type = InputType.Submit,
        caption = getText("page.settings.account.add.mobile.button.next"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsAccountUrl()}/add/mobile/pages/phone/submit"),
        ),
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitPhoneRequest): Action {
        // Validate
        val providers = checkoutManagerApi.searchPaymentProvider(
            request = SearchPaymentProviderRequest(
                number = request.phoneNumber,
                type = PaymentMethodType.MOBILE_MONEY.name,
            ),
        ).paymentProviders
        if (providers.isEmpty()) {
            return promptError("prompt.error.phone-not-valid-mobile-money")
        }

        // OTP
        val response = securityManager.createOtp(
            request = CreateOTPRequest(
                address = request.phoneNumber,
                type = MessagingType.SMS.name,
            ),
        )

        // Save
        val provider = providers[0]
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        dao.save(
            AccountEntity(
                number = request.phoneNumber,
                ownerName = member.displayName,
                type = provider.type,
                providerId = provider.id,
                otpToken = response.token,
            ),
        )

        return gotoNextPage()
    }
}
