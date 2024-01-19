package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.form.ProductAttributeForm
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.SearchCategoryRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping
class StoreProductController(
    private val productService: ProductService,
    private val categoryService: CategoryService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    override fun pageName() = PageName.STORE_PRODUCT

    @GetMapping("/me/store/products/{id}")
    fun index(
        @PathVariable id: Long,
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        val store = checkStoreAccess()
        model.addAttribute("store", store)
        if (error != null) {
            model.addAttribute(
                "error",
                requestContext.getMessage(
                    error,
                    "error.unexpected",
                    emptyArray(),
                    LocaleContextHolder.getLocale()
                )
            )
        }

        val country = Country.all.find { country -> country.currency.equals(store.currency, true) }
        model.addAttribute("country", country)

        val categories = categoryService.search(
            SearchCategoryRequest(
                limit = 200
            )
        ).sortedBy { it.longTitle }
        model.addAttribute("categories", categories)

        val product = productService.get(id)
        model.addAttribute("product", product)
        model.addAttribute("submitUrl", "/me/store/products/${product.id}")
        if (productService.canStream(product)) {
            model.addAttribute("previewUrl", "/me/store/products/${product.id}/preview")
        }

        return "admin/store/product/product"
    }

    @ResponseBody
    @PostMapping("/me/store/products/{id}", produces = ["application/json"], consumes = ["application/json"])
    fun submit(@PathVariable id: Long, @RequestBody form: ProductAttributeForm): Map<String, String> {
        productService.updateAttribute(id, form)
        return emptyMap()
    }

    @GetMapping("/me/store/products/{id}/publish")
    fun publish(@PathVariable id: Long, model: Model): String {
        try {
            productService.publish(id)
            return "redirect:/me/store/products/$id"
        } catch (ex: Exception) {
            val error = toErrorKey(ex)
            return "redirect:/me/store/products/$id?error=$error"
        }
    }
}
