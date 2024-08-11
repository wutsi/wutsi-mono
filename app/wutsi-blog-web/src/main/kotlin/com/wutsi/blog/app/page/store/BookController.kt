package com.wutsi.blog.app.page.store

import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.SearchBookRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class BookController(
    private val bookService: BookService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.BOOK

    @GetMapping("/me/book")
    fun index(@RequestParam(name = "transaction-id") transactionId: String, model: Model): String {
        val book = findBook(transactionId)
        return if (book == null) {
            model.addAttribute("transactionId", transactionId)
            "reader/book"
        } else {
            "redirect:/me/play/${book.id}"
        }
    }

    @ResponseBody
    @GetMapping("/me/book/check")
    fun check(@RequestParam(name = "transaction-id") transactionId: String): Map<String, String> {
        val book = findBook(transactionId)
        return if (book == null) {
            return emptyMap()
        } else {
            mapOf("url" to "/me/play/${book.id}")
        }
    }

    private fun findBook(transactionId: String): BookModel? =
        bookService.search(
            SearchBookRequest(transactionId = transactionId, limit = 1)
        ).firstOrNull()
}
