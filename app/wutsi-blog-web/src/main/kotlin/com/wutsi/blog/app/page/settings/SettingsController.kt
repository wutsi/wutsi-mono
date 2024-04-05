package com.wutsi.blog.app.page.settings

import com.wutsi.blog.app.form.ImportForm
import com.wutsi.blog.app.form.SubscribeForm
import com.wutsi.blog.app.form.UnsubscribeForm
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.SubscriptionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.Locale

@Controller
@RequestMapping("/me/settings")
class SettingsController(
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS

    @GetMapping
    fun index(
        @RequestParam(required = false) highlight: String? = null,
        model: Model,
    ): String {
        val blog = requestContext.currentUser()

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .sortedBy { it.displayLanguage }
        model.addAttribute("languages", languages)
        model.addAttribute(
            "defaultCountry",
            blog?.country?.let { Country.fromCode(blog.country) } ?: Country.CM
        )

        model.addAttribute("highlight", highlight)
        blog?.let { model.addAttribute("wallet", getWallet(blog)) }
        model.addAttribute("countryCodeCSV", Country.all.map { it.code }.joinToString(separator = ","))
        return "settings/profile"
    }

    @ResponseBody
    @PostMapping(produces = ["application/json"], consumes = ["application/json"])
    fun set(@RequestBody request: UserAttributeForm): Map<String, Any?> =
        try {
            if (request.name == "wallet_account_number") {
                walletService.updateAccount(request)
            } else {
                userService.updateAttribute(request)
            }
            mapOf("id" to requestContext.currentUser()?.id)
        } catch (ex: Exception) {
            val key = errorKey(ex)
            mapOf(
                "id" to requestContext.currentUser()?.id,
                "error" to requestContext.getMessage(key),
            )
        }

    @ResponseBody
    @PostMapping("/subscribe", produces = ["application/json"], consumes = ["application/json"])
    fun subscribe(@RequestBody request: SubscribeForm): Map<String, Any?> {
        val userId = requestContext.currentUser()?.id

        if (userId != null) {
            request.email.split(",").forEach {
                subscriptionService.subscribe(
                    userId = userId,
                    email = it.trim(),
                    referer = "settings",
                )
            }
        }
        return emptyMap()
    }

    @ResponseBody
    @PostMapping("/import-subscribers", produces = ["application/json"], consumes = ["application/json"])
    fun importSubscribers(@RequestBody request: ImportForm): Map<String, Any?> {
        subscriptionService.import(request.url)
        return emptyMap()
    }

    @RequestMapping("/subscriptions")
    fun subscriptions(model: Model): String {
        val blog = requestContext.currentUser()
        val subscriptions = subscriptionService.search(
            SearchSubscriptionRequest(
                subscriberId = blog!!.id,
                limit = 20,
            )
        )
        if (subscriptions.isNotEmpty()) {
            model.addAttribute(
                "subscriptions",
                userService.search(
                    SearchUserRequest(
                        userIds = subscriptions.map { it.userId },
                        limit = subscriptions.size
                    )
                ).sortedBy { it.fullName }
            )
        }
        return "settings/fragment/subscriptions"
    }

    @ResponseBody
    @GetMapping("/unsubscribe", produces = ["application/json"])
    fun unsubscribe(@RequestParam("user-id") userId: Long): Map<String, Any?> {
        subscriptionService.unsubscribe(
            UnsubscribeForm(
                userId = userId,
                subscriberId = requestContext.currentUser()?.id ?: -1,
            )
        )
        return emptyMap()
    }
}
