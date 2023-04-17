package com.wutsi.application.common.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.Page
import com.wutsi.application.service.URLBuilder
import com.wutsi.application.widget.BottomNavigationBarWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Navigate
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.flutter.sdui.enums.DialogType.Error
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.WutsiException
import com.wutsi.platform.core.logging.KVLogger
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URLEncoder

abstract class AbstractEndpoint {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Value("\${wutsi.application.shell-url}")
    protected lateinit var shellUrl: String

    @Value("\${wutsi.application.asset-url}")
    protected lateinit var assertUrl: String

    @Value("\${wutsi.feature-flags.fundraising}")
    protected lateinit var enableFundraising: String

    @Value("\${wutsi.application.webapp-url}")
    protected lateinit var webappUrl: String

    fun getLogoUrl() = "$assertUrl/logo/wutsi.png"

    fun getNoPictureUrl() = "$assertUrl/images/nopicture.png"

    @ExceptionHandler(Throwable::class)
    fun onException(ex: Throwable): Action {
        val action = Action(
            type = Prompt,
            prompt = Dialog(
                title = getText("prompt.error.title"),
                type = Error,
                message = getErrorMessage(ex),
            ).toWidget(),
        )
        log(action, ex)
        return action
    }

    private fun getErrorMessage(ex: Throwable): String {
        if (ex is FeignException) {
            try {
                val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
                return getText(response.error.code)
            } catch (e: Exception) {
                // Ignore
            }
        }
        return getText("message.error.unexpected-error")
    }

    protected fun createBottomNavigationBarWidget(member: Member) = BottomNavigationBarWidget(
        profileUrl = urlBuilder.build(Page.getProfileUrl()),
        ordersUrl = member.businessId?.let { urlBuilder.build(Page.getOrderListUrl()) },
        donationUrl = member.fundraisingId?.let {
            if (enableFundraising.toBoolean()) {
                urlBuilder.build(Page.getDonationListUrl())
            } else {
                null
            }
        },
        storeId = member.businessId?.let { member.storeId },
    ).toBottomNavigationBar()

    private fun log(action: Action, e: Throwable) {
        log(e)
        logger.add("action_type", action.type)
        logger.add("action_url", action.url)
        logger.add("action_prompt_type", action.prompt?.type)
        logger.add("action_prompt_message", action.prompt?.attributes?.get("message"))
    }

    protected fun log(e: Throwable) {
        logger.setException(e)
        if (e is WutsiException) {
            logger.add("error_code", e.error.code)
        }
    }

    protected fun gotoPreviousScreen(): Action =
        gotoRoute("/..")

    protected fun gotoOnboard(): Action =
        gotoUrl(url = urlBuilder.build(Page.getOnboardUrl()))

    protected fun executeCommand(url: String, parameters: Map<String, String>? = null, confirm: String? = null) =
        Action(
            type = ActionType.Command,
            url = url,
            parameters = parameters,
            prompt = confirm?.let {
                Dialog(
                    type = DialogType.Confirm,
                    message = confirm,
                ).toWidget()
            },
        )

    protected fun navigateTo(url: String) = Action(
        type = Navigate,
        url = url,
    )

    protected fun gotoLogin(
        phoneNumber: String,
        title: String? = null,
        subTitle: String? = null,
        hideBackButton: Boolean? = null,
        auth: Boolean? = null,
        darkMode: String? = null,
        hideChangeAccount: Boolean = true,
    ): Action {
        val url = StringBuilder(
            Page.getLoginUrl() + "?title=" + encodeURLParam(title ?: "") +
                "&sub-title=" + encodeURLParam(subTitle ?: getText("page.login.sub-title")) +
                "&phone=" + encodeURLParam(phoneNumber) +
                "&return-to-route=true" +
                "&hide-change-account-button=$hideChangeAccount",
        )
        hideBackButton?.let { url.append("&hide-back-button=$it") }
        auth?.let { url.append("&auth=$it") }
        darkMode?.let { url.append("&dark-mode=$it") }

        return gotoUrl(
            url = urlBuilder.build(url.toString()),
            type = ActionType.Route,
            replacement = true,
        )
    }

    protected fun gotoPage(page: Int) = Action(
        type = ActionType.Page,
        url = "page:/$page",
    )

    protected fun gotoRoute(path: String, replacement: Boolean? = null, parameters: Map<String, String>? = null) =
        Action(
            type = Route,
            url = "route:$path",
            replacement = replacement,
            parameters = parameters,
        )

    protected fun gotoUrl(
        url: String,
        type: ActionType = ActionType.Route,
        replacement: Boolean? = null,
        parameters: Map<String, String>? = null,
    ) = Action(
        type = type,
        url = url,
        replacement = replacement,
        parameters = parameters,
    )

    protected fun promptError(errorKey: String) = Action(
        type = ActionType.Prompt,
        prompt = Dialog(
            title = getText("prompt.error.title"),
            type = DialogType.Error,
            message = getText(errorKey),
        ).toWidget(),
    )

    protected fun promptInfo(errorKey: String) = Action(
        type = ActionType.Prompt,
        prompt = Dialog(
            title = getText("prompt.info.title"),
            type = DialogType.Information,
            message = getText(errorKey),
        ).toWidget(),
    )

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, LocaleContextHolder.getLocale())

    protected fun formattedPhoneNumber(phoneNumber: String?, country: String? = null): String? {
        if (phoneNumber == null) {
            return null
        }

        val phoneUtil = PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(phoneNumber, country ?: "")
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    protected fun encodeURLParam(text: String?): String =
        text?.let { URLEncoder.encode(it, "utf-8") } ?: ""
}
