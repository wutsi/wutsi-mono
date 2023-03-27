package com.wutsi.application.checkout.settings.account.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.checkout.settings.account.dto.SubmitCashoutRequest
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.CreateCashoutRequest
import com.wutsi.checkout.manager.dto.SearchPaymentMethodRequest
import com.wutsi.enums.PaymentMethodStatus
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Form
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.regulation.RegulationEngine
import feign.FeignException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/settings/2/accounts/cashout")
class Settings2AccountCashoutScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        if (member.businessId == null) {
            return Container().toWidget()
        }

        val business = checkoutManagerApi.getBusiness(member.businessId!!).business
        val paymentMethods = checkoutManagerApi.searchPaymentMethod(
            request = SearchPaymentMethodRequest(
                status = PaymentMethodStatus.ACTIVE.name,
                limit = 30,
            ),
        ).paymentMethods
        val country = regulationEngine.country(business.country)
        val fmt = country.createMoneyFormat()

        return Screen(
            id = Page.SETTINGS_ACCOUNT_CASHOUT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.cashout.app-bar.title", arrayOf(fmt.format(business.balance))),
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = listOfNotNull(
                        Container(
                            padding = 10.0,
                            child = Text(getText("page.settings.account.cashout.balance")),
                        ),
                        Container(
                            padding = 10.0,
                            child = MoneyText(
                                value = business.cashoutBalance.toDouble(),
                                currency = country.currencySymbol,
                                color = Theme.COLOR_PRIMARY,
                                numberFormat = country.numberFormat,
                                locale = country.locale,
                            ),
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        Form(
                            children = listOf(
                                Container(
                                    padding = 10.0,
                                    child = DropdownButton(
                                        name = "token",
                                        value = if (paymentMethods.size == 1) paymentMethods[0].token else null,
                                        required = true,
                                        children = paymentMethods.map {
                                            DropdownMenuItem(
                                                caption = it.number,
                                                icon = it.provider.logoUrl,
                                                value = it.token,
                                            )
                                        },
                                    ),
                                ),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "amount",
                                        required = true,
                                        type = InputType.Number,
                                        caption = getText("page.settings.account.cashout.field.amount"),
                                        inputFormatterRegex = "[0-9]",
                                        action = executeCommand(
                                            url = urlBuilder.build("${Page.getSettingsAccountCashoutUrl()}/submit"),
                                        ),
                                    ),
                                ),
                                Container(padding = 20.0),
                                Container(
                                    padding = 10.0,
                                    child = Input(
                                        name = "submit",
                                        type = InputType.Submit,
                                        caption = getText("page.settings.account.cashout.button.submit"),
                                        action = executeCommand(
                                            url = urlBuilder.build("${Page.getSettingsAccountCashoutUrl()}/submit"),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/submit")
    fun delete(@RequestBody request: SubmitCashoutRequest): Action {
        try {
            checkoutManagerApi.createCashout(
                request = CreateCashoutRequest(
                    paymentMethodToken = request.token,
                    amount = request.amount,
                    idempotencyKey = UUID.randomUUID().toString(),
                ),
            )
            return gotoPreviousScreen()
        } catch (ex: FeignException) {
            try {
                val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
                return promptError("error-message.${response.error.downstreamCode}")
            } catch (ex2: Exception) {
                throw ex
            }
        }
    }
}
