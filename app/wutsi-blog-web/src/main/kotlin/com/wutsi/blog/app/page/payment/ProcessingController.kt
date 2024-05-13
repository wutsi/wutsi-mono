package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.transaction.dto.TransactionType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/processing")
class ProcessingController(
    private val service: TransactionService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName(): String = PageName.PROCESSING

    @GetMapping
    fun index(
        @RequestParam(name = "id") transactionId: String,
        @RequestParam(required = false) redirect: String? = null,
        model: Model,
    ): String {
        val tx = service.get(transactionId, true)

        model.addAttribute("tx", tx)
        model.addAttribute(
            "page",
            createPage(
                title = requestContext.getMessage("page.processing.title"),
                description = "",
            ),
        )

        if (tx.type == TransactionType.CHARGE) {
            model.addAttribute(
                "downloadUrl",
                "/product/${tx.product?.id}/download/${tx.id}"
            )
        } else if (redirect != null) {
            model.addAttribute("redirect", redirect)
            model.addAttribute("redirectToStory", redirect.startsWith("/read/"))
        }

        model.addAttribute(
            "tryAgainUrl",
            when (tx.type) {
                TransactionType.CHARGE -> tx.product?.id?.let { id -> "/buy?product-id=$id" }
                TransactionType.DONATION -> tx.merchant?.let { merchant -> "${merchant.slug}/donate" }
                TransactionType.PAYMENT -> tx.adsId?.let { id -> "/me/ads/pay/?ads-id=$id" }
                else -> null
            }
        )

        return "payment/processing"
    }

    @ResponseBody
    @GetMapping("/status")
    fun get(@RequestParam("id") transactionId: String): TransactionModel =
        service.get(transactionId, true)
}
