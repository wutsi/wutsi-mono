package com.wutsi.blog.app.page.settings

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.model.WalletModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.service.WalletService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/settings")
class SettingsController(
    private val userService: UserService,
    private val walletService: WalletService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS

    @GetMapping
    fun index(
        @RequestParam(required = false) highlight: String? = null,
        model: Model,
    ): String {
        model.addAttribute("highlight", highlight)
        model.addAttribute("wallet", getWallet())
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

    private fun getWallet(): WalletModel? =
        requestContext.currentUser()?.let { user ->
            user.walletId?.let { walletId -> walletService.get(walletId) }
        }
}
