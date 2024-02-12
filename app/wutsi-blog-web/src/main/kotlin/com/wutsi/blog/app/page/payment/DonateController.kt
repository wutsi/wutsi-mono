package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.form.DonateForm
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.CountryService
import com.wutsi.blog.app.service.ImageType
import com.wutsi.blog.app.service.OpenGraphImageGenerator
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.UUID

@Controller
class DonateController(
    private val userService: UserService,
    private val transactionService: TransactionService,
    private val logger: KVLogger,
    private val opengraph: OpenGraphImageGenerator,
    private val imageService: ImageService,
    private val countryService: CountryService,
    @Value("\${wutsi.paypal.client-id}") private val paypalClientId: String,

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
        @RequestParam(required = false) redirect: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(name)
        return donate(blog, error, redirect, model)
    }

    @GetMapping("/me/donate/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) error: String? = null,
        @RequestParam(required = false) redirect: String? = null,
        model: Model,
    ): String {
        val blog = userService.get(id)
        return donate(blog, error, null, model)
    }

    fun donate(blog: UserModel, error: String?, redirect: String?, model: Model): String {
        if (!blog.blog) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_NOT_BLOG,
                ),
            )
        }

        val wallet = getWallet(blog)
            ?: throw NotFoundException(
                error = Error(
                    code = ErrorCode.USER_HAS_NO_WALLET,
                ),
            )

        val country = Country.all.find { it.code == wallet.country.code }
            ?: throw NotFoundException(
                error = Error(
                    code = "invalid_country",
                    data = mapOf(
                        "country" to wallet.country,
                    ),
                ),
            )

        val form = DonateForm(
            amount = country.defaultDonation,
            email = requestContext.currentUser()?.email ?: "",
            fullName = requestContext.currentUser()?.fullName ?: "",
            idempotencyKey = UUID.randomUUID().toString(),
            name = blog.name,
            error = error?.let { requestContext.getMessage(error) },
            country = country.code,
            redirect = redirect,
        )
        model.addAttribute("form", form)
        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        model.addAttribute("amount", country.defaultDonation)
        model.addAttribute("email", requestContext.currentUser()?.email ?: "")
        model.addAttribute("idempotencyKey", UUID.randomUUID().toString())
        model.addAttribute("wallet", wallet)
        model.addAttribute("countryCodeCSV", Country.all.map { it.code }.joinToString(separator = ","))
        model.addAttribute("paypalClientId", paypalClientId)

        val fmt = country.createMoneyFormat()
        var i = 0
        country.defaultDonationAmounts.forEach { amount ->
            val amountText = fmt.format(amount)
            val amountButton = requestContext.getMessage(
                key = "button.donate_with_amount",
                args = arrayOf(amountText),
            )

            i++
            model.addAttribute("amount$i", amount)
            model.addAttribute("amount${i}Text", amountText)
            model.addAttribute("amount${i}Button", amountButton)

            if (amount == country.defaultDonation) {
                model.addAttribute("amountButton", amountButton)
            }
        }

        val store = getStore(blog)
        model.addAttribute("store", store)
        model.addAttribute("paymentProviderTypes", countryService.paymentProviderTypes)
        return "payment/donate"
    }

    @PostMapping("/donate/submit")
    fun submit(
        @ModelAttribute form: DonateForm,
        model: Model,
    ): String {
        try {
            val transactionId = transactionService.donate(form)
            logger.add("transaction_id", transactionId)
            return if (form.redirect == null) {
                "redirect:/processing?id=$transactionId"
            } else {
                "redirect:/processing?id=$transactionId&redirect=" + URLEncoder.encode(form.redirect, "utf-8")
            }
        } catch (ex: Exception) {
            LOGGER.error("Donation to User#${form.name} failed", ex)
            return "redirect:/@/${form.name}/donate?error=" + toErrorKey(ex)
        }
    }

    @GetMapping("/@/{name}/donate.png")
    fun image(@PathVariable name: String): ResponseEntity<InputStreamResource> {
        val blog = userService.get(name)
        if (!blog.blog || blog.walletId.isNullOrEmpty()) {
            return ResponseEntity.notFound().build()
        }

        val out = ByteArrayOutputStream()
        opengraph.generate(
            type = ImageType.DONATION,
            pictureUrl = blog.pictureUrl?.let { pictureUrl ->
                imageService.transform(
                    url = pictureUrl,
                    transformation = Transformation(
                        Dimension(
                            OpenGraphImageGenerator.IMAGE_WIDTH,
                            OpenGraphImageGenerator.IMAGE_HEIGHT,
                        ),
                    ),
                )
            },
            title = blog.fullName,
            description = blog.biography,
            language = blog.language,
            output = out,
        )

        val input = ByteArrayInputStream(out.toByteArray())
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(InputStreamResource(input))
    }

    private fun getPage(user: UserModel) = createPage(
        description = requestContext.getMessage("page.donate.description"),
        title = requestContext.getMessage("page.donate.title") + " | ${user.fullName}",
        imageUrl = "$baseUrl/@/${user.name}/donate.png",
        url = url(user) + "/donate",
    )
}
