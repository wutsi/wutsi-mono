package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.SearchBookRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LibraryController(
    private val bookService: BookService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIBRARY

    @GetMapping("/me/library")
    fun index(model: Model): String {
        val books = bookService.search(
            SearchBookRequest(
                userId = requestContext.currentUser()?.id ?: -1,
                limit = 50
            )
        )
        if (books.isNotEmpty()) {
            model.addAttribute("books", books)
        }
        return "reader/library"
    }
}
