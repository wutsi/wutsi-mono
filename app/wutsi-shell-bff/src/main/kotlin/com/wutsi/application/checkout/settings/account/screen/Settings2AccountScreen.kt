package com.wutsi.application.checkout.settings.account.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.PhoneUtil
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.enums.PaymentMethodType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/settings/2/accounts")
class Settings2AccountScreen(
    private val checkoutManagerApi: CheckoutManagerApi,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(@RequestParam token: String): Widget {
        val paymentMethod = checkoutManagerApi.getPaymentMethod(token).paymentMethod
        return Screen(
            id = Page.SETTINGS_ACCOUNT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.account.app-bar.title"),
            ),
            child = SingleChildScrollView(
                child = Column(
                    children = listOfNotNull(
                        Container(padding = 20.0),
                        toRowWidget(
                            "page.settings.account.provider",
                            Row(
                                mainAxisAlignment = MainAxisAlignment.start,
                                crossAxisAlignment = CrossAxisAlignment.center,
                                children = listOfNotNull(
                                    Image(
                                        width = 32.0,
                                        height = 32.0,
                                        url = paymentMethod.provider.logoUrl,
                                    ),
                                    Container(padding = 5.0),
                                    Text(paymentMethod.provider.name),
                                ),
                            ),
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        if (paymentMethod.country.isNullOrEmpty()) {
                            null
                        } else {
                            toRowWidget(
                                key = "page.settings.account.country",
                                value = Locale(
                                    LocaleContextHolder.getLocale().language,
                                    paymentMethod.country,
                                ).displayCountry,
                            )
                        },
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        toRowWidget(
                            key = "page.settings.account.number",
                            value = if (paymentMethod.type == PaymentMethodType.MOBILE_MONEY.name) {
                                PhoneUtil.format(paymentMethod.number)
                            } else {
                                paymentMethod.number
                            },
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        toRowWidget(
                            key = "page.settings.account.owner",
                            value = paymentMethod.ownerName.uppercase(),
                        ),
                        Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                        Container(
                            padding = 10.0,
                            child = Button(
                                caption = getText("page.settings.account.button.delete"),
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsAccountUrl()}/delete"),
                                    parameters = mapOf(
                                        "token" to token,
                                    ),
                                    confirm = getText("page.settings.account.confirm.delete"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/delete")
    fun delete(@RequestParam token: String): Action {
        checkoutManagerApi.removePaymentMethod(token)
        return gotoPreviousScreen()
    }

    private fun toRowWidget(key: String, value: String?): WidgetAware =
        toRowWidget(key, Text(value ?: ""))

    private fun toRowWidget(key: String, value: WidgetAware): WidgetAware =
        Row(
            children = listOf(
                Flexible(
                    flex = 2,
                    child = Container(
                        padding = 10.0,
                        child = Text(
                            getText(key),
                            bold = true,
                            alignment = TextAlignment.Right,
                        ),
                    ),
                ),
                Flexible(
                    flex = 3,
                    child = value,
                ),
            ),
        )
}
