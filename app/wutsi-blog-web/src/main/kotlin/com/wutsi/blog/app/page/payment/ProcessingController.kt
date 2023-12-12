package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.util.PageName
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
    override fun pageName(): String = PageName.DONATE_PROCESSING

    @GetMapping
    fun index(
        @RequestParam(name = "id") transactionId: String,
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

        return "payment/processing"
    }

    @ResponseBody
    @GetMapping("/status")
    fun get(@RequestParam("id") transactionId: String): TransactionModel =
        service.get(transactionId, true)
}
