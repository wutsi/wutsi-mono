package com.wutsi.blog.app.page.payment

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        const val LIMIT = 30
    }

    override fun pageName() = PageName.TRANSACTIONS

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("page", getPage())

        val user = requestContext.currentUser()
        user?.let {
            model.addAttribute("wallet", getWallet(user))
        }

        more(0, model)
        return "payment/transactions"
    }

    @GetMapping("/more")
    fun more(@RequestParam(required = false) offset: Int = 0, model: Model): String {
        val transactions = transactionService.search(LIMIT, offset)
        model.addAttribute("transactions", transactions)
        if (transactions.size >= LIMIT) {
            model.addAttribute("moreUrl", "/me/transactions/more?offset=" + (offset + LIMIT))
        }
        return "payment/fragment/transactions"
    }
}
