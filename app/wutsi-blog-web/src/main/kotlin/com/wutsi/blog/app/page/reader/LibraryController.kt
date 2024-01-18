package com.wutsi.blog.app.page.reader

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LibraryController(
    private val bookService: BookService,
    private val productService: ProductService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIBRARY

    @GetMapping("/me/library")
    fun index(model: Model): String {
        val books = loadBooks(model)
        loadProducts(books, model)
        return "reader/library"
    }

    fun loadBooks(model: Model): List<BookModel> {
        val books = bookService.search(
            SearchBookRequest(
                userId = requestContext.currentUser()?.id ?: -1,
                limit = 50
            )
        )
        if (books.isNotEmpty()) {
            model.addAttribute("books", books)
        }
        return books
    }

    fun loadProducts(books: List<BookModel>, model: Model): List<ProductModel> {
        val products = productService.search(
            request = SearchProductRequest(
                status = ProductStatus.PUBLISHED,
                excludeProductIds = books.map { book -> book.product.id },
                available = true,
                limit = 4,
                sortBy = ProductSortStrategy.ORDER_COUNT,
                sortOrder = SortOrder.DESCENDING
            ),
            bubbleDownPurchasedProducts = false
        )

        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return products
    }
}
