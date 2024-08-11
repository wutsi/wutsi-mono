package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.BookService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class LibraryController(
    private val bookService: BookService,
    private val productService: ProductService,
    private val userService: UserService,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.LIBRARY

    @GetMapping("/me/library")
    fun index(model: Model): String {
        val user = requestContext.currentUser()!!
        val books = loadBooks(user, model)

        val storeIds = books.map { book -> book.product.storeId }.toSet().toList()
        if (storeIds.isNotEmpty()) {
            model.addAttribute("storeIds", storeIds)
        }

        loadProducts(user, storeIds, model)

        return "reader/library"
    }

    @GetMapping("/me/library/stores/{store-id}")
    fun store(@PathVariable(name = "store-id") storeId: String, model: Model): String {
        val store = storeService.get(storeId)
        val user = userService.get(store.userId)

        val products = productService.search(
            request = SearchProductRequest(
                status = ProductStatus.PUBLISHED,
                types = listOf(ProductType.COMICS, ProductType.EBOOK),
                storeIds = listOf(storeId),
                available = true,
                limit = 4,
                sortBy = ProductSortStrategy.PUBLISHED,
                excludePurchasedProduct = true,
                searchContext = SearchProductContext(
                    userId = requestContext.currentUser()?.id
                )
            ),
        )

        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
            model.addAttribute("merchant", user)
            model.addAttribute("store", store)
        }

        return "reader/fragment/library-store"
    }

    fun loadBooks(user: UserModel, model: Model): List<BookModel> {
        val books = bookService.search(
            SearchBookRequest(
                userId = user.id,
                limit = 50
            )
        ).filter { book -> !book.expired }
            .distinctBy { it.product.id }
        if (books.isNotEmpty()) {
            model.addAttribute("books", books)
        }
        return books
    }

    fun loadProducts(user: UserModel, storeIds: List<String>, model: Model): List<ProductModel> {
        val store = getStore(user)
        val products = productService.search(
            request = SearchProductRequest(
                status = ProductStatus.PUBLISHED,
                types = listOf(ProductType.COMICS, ProductType.EBOOK),
                available = true,
                limit = 100,
                sortBy = ProductSortStrategy.RECOMMENDED,
                excludePurchasedProduct = true,
                dedupUser = true,
                searchContext = SearchProductContext(
                    userId = requestContext.currentUser()?.id,
                )
            ),
        ).filter { product -> !storeIds.contains(product.storeId) && product.storeId != store?.id }

        if (products.isNotEmpty()) {
            model.addAttribute("products", products)
        }
        return products
    }
}
