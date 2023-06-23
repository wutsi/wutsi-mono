package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.DonateForm
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.service.WalletService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class DonateController(
    private val userService: UserService,
    private val walletService: WalletService,
    private val transactionService: TransactionService,
    private val logger: KVLogger,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DonateController::class.java)
    }

    override fun pageName() = PageName.DONATE

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}/donate")
    fun index(
        @PathVariable name: String,
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        blog.walletId
            ?: return "redirect:/@/$name" // No Monetization

        val wallet = walletService.get(blog.walletId)
        val country = Country.all.find { it.code == wallet.country.code }
            ?: return "redirect:/@/$name" // Can't accept donation

        val form = DonateForm(
            amount = country.donationBaseAmount,
            email = requestContext.currentUser()?.email ?: "",
            fullName = requestContext.currentUser()?.fullName ?: "",
            idempotencyKey = UUID.randomUUID().toString(),
            name = name,
            error = error?.let { requestContext.getMessage(error) },
        )
        model.addAttribute("form", form)
        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        model.addAttribute("amount", country.donationBaseAmount)
        model.addAttribute("email", requestContext.currentUser()?.email ?: "")
        model.addAttribute("idempotencyKey", UUID.randomUUID().toString())
        model.addAttribute("wallet", wallet)

        val fmt = country.createMoneyFormat()
        for (i in 1..4) {
            val amount = i * country.donationBaseAmount
            val amountText = fmt.format(amount)
            val amountButton = requestContext.getMessage(
                key = "button.donate",
                args = arrayOf(amountText),
            )
            model.addAttribute("amount$i", amount)
            model.addAttribute("amount${i}Text", amountText)
            model.addAttribute("amount${i}Button", amountButton)

            if (i == 1) {
                model.addAttribute("amountButton", amountButton)
            }
        }
        return "payment/donate"
    }

    @PostMapping("/donate/submit")
    fun submit(
        @ModelAttribute form:
        DonateForm,
        model: Model,
    ): String {
        try {
            val transactionId = transactionService.donate(form)
            logger.add("transaction_id", transactionId)
            return "redirect:/processing?id=$transactionId"
        } catch (ex: Exception) {
            LOGGER.error("Donation to User#${form.name} failed")
            return "redirect:/@/${form.name}/donate?error=" + toErrorKey(ex)
        }
    }

    private fun toErrorKey(ex: Exception): String = "error.unexpected"

    private fun getPage(user: UserModel) = createPage(
        description = requestContext.getMessage("page.donate.description"),
        title = requestContext.getMessage("page.donate.description") + " | ${user.fullName}",
        imageUrl = user.pictureUrl,
    )
}
