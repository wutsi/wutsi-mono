package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.form.CreateProductForm
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/store/products/create")
class CreateProductController(
    private val productService: ProductService,
    private val categoryService: CategoryService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    override fun pageName() = PageName.STORE_PRODUCT_CREATE

    @GetMapping
    fun index(model: Model): String {
        val store = checkStoreAccess()
        model.addAttribute("store", store)

        val country = Country.all.find { country -> country.currency.equals(store.currency, true) }
        model.addAttribute("country", country)

        val categories = categoryService.all()
        model.addAttribute("categories", categories)

        model.addAttribute("form", CreateProductForm())

        return "admin/store/product/create"
    }

    @PostMapping
    fun submit(@ModelAttribute form: CreateProductForm): String {
        val store = checkStoreAccess()
        val productId = productService.create(store, form)
        return "redirect:/me/store/products/$productId"
    }
}
