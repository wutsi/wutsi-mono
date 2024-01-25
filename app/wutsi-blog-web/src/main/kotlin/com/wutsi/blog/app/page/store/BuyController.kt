package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.form.BuyForm
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class BuyController(
    private val userService: UserService,
    private val productService: ProductService,
    private val transactionService: TransactionService,
    private val logger: KVLogger,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(BuyController::class.java)
    }

    override fun pageName() = PageName.BUY

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/buy")
    fun index(
        @RequestParam("product-id") productId: Long,
        @RequestParam(required = false) error: String? = null,
        @RequestParam(name = "t", required = false) transactionId: String? = null,
        model: Model,
    ): String {
        val product = productService.get(productId)
        val store = storeService.get(product.storeId)
        val blog = userService.get(store.userId)
        val wallet = blog.walletId?.let { walletService.get(blog.walletId) }
        val user = requestContext.currentUser()
        val tx = transactionId?.let { resolveTransaction(transactionId) }

        val form = BuyForm(
            amount = product.offer.price.value,
            email = tx?.email ?: user?.email ?: "",
            fullName = tx?.paymentMethodOwner ?: user?.fullName ?: "",
            idempotencyKey = UUID.randomUUID().toString(),
            productId = productId,
            error = error?.let { requestContext.getMessage(error) },
            country = wallet?.country?.code ?: "",
        )

        model.addAttribute("form", form)
        model.addAttribute("blog", blog)
        model.addAttribute("product", product)
        model.addAttribute("wallet", wallet)
        model.addAttribute("idempotencyKey", UUID.randomUUID().toString())
        return "store/buy"
    }

    @PostMapping("/buy/submit")
    fun submit(
        @ModelAttribute form: BuyForm,
        model: Model,
    ): String {
        try {
            val transactionId = transactionService.buy(form)
            logger.add("transaction_id", transactionId)
            return "redirect:/processing?id=$transactionId"
        } catch (ex: Exception) {
            LOGGER.error("Purchase failed", ex)
            return "redirect:/buy?product-id=${form.productId}&error=" + toErrorKey(ex)
        }
    }

    private fun resolveTransaction(id: String): TransactionModel? =
        try {
            transactionService.get(id, false)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve transaction#$id", ex)
            null
        }
}
