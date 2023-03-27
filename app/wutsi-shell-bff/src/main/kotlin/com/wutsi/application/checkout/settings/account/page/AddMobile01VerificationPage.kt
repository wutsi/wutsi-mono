package com.wutsi.application.checkout.settings.account.page

import com.wutsi.application.Page
import com.wutsi.application.checkout.settings.account.dao.AccountRepository
import com.wutsi.application.checkout.settings.account.dto.SubmitOTPRequest
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.service.CountryDetector
import com.wutsi.application.util.PhoneUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.AddPaymentMethodRequest
import com.wutsi.enums.PaymentMethodType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/accounts/add/mobile/pages/verification")
class AddMobile01VerificationPage(
    private val dao: AccountRepository,
    private val countryDetector: CountryDetector,
    private val checkoutManagerApi: CheckoutManagerApi,
    private val securityManager: SecurityManagerApi,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.account.add.mobile.verification.title")

    override fun getSubTitle(): String? = getText(
        "page.settings.account.add.mobile.verification.sub-title",
        arrayOf(PhoneUtil.format(dao.get().number)),
    )

    override fun getBody() = Container(
        padding = 10.0,
        child = PinWithKeyboard(
            name = "code",
            hideText = true,
            pinSize = 20.0,
            keyboardButtonSize = 70.0,
            action = executeCommand(
                url = urlBuilder.build("${Page.getSettingsAccountUrl()}/add/mobile/pages/verification/submit"),
            ),
        ),
    )

    override fun getButton() = Button(
        caption = getText("page.settings.account.add.mobile.button.resend"),
        type = ButtonType.Text,
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsAccountUrl()}/add/mobile/pages/verification/resend"),
        ),
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitOTPRequest): Action {
        // Validate
        val account = dao.get()
        securityManager.verifyOtp(
            token = account.otpToken,
            request = VerifyOTPRequest(
                code = request.code,
            ),
        )

        // Save
        checkoutManagerApi.addPaymentMethod(
            request = AddPaymentMethodRequest(
                providerId = account.providerId,
                type = account.type,
                ownerName = account.ownerName,
                number = account.number,
                country = countryDetector.detect(account.number),
            ),
        )
        return gotoNextPage()
    }

    @PostMapping("/resend")
    fun resend(): Action {
        val account = dao.get()
        account.otpToken = securityManager.createOtp(
            request = CreateOTPRequest(
                address = account.number,
                type = PaymentMethodType.MOBILE_MONEY.name,
            ),
        ).token
        dao.save(account)

        return promptInfo(
            getText(
                key = "page.settings.account.add.mobile.verification.resent",
                args = arrayOf(PhoneUtil.format(account.number)),
            ),
        )
    }
}
