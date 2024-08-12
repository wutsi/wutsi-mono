package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.form.ProductAttributeForm
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.CategoryService
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.error.ErrorCode
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.ConflictException
import org.slf4j.LoggerFactory
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
import java.io.IOException
import java.net.URL

@Controller
@RequestMapping
class StoreProductController(
    private val productService: ProductService,
    private val categoryService: CategoryService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoreProductController::class.java)
    }

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

        val categories = categoryService.all()
        model.addAttribute("categories", categories)

        val product = productService.get(id)
        model.addAttribute("product", product)
        model.addAttribute("submitUrl", "/me/store/products/${product.id}")
        if (product.streamable && !product.processingFile) {
            model.addAttribute("previewUrl", "/me/store/products/${product.id}/preview")
        }

        return "admin/store/product/product"
    }

    @ResponseBody
    @PostMapping("/me/store/products/{id}", produces = ["application/json"], consumes = ["application/json"])
    fun submit(@PathVariable id: Long, @RequestBody form: ProductAttributeForm): Map<String, Any?> {
        return try {
            validate(form)
            productService.updateAttribute(id, form)
            emptyMap()
        } catch (ex: Exception) {
            val key = errorKey(ex)
            mapOf(
                "id" to requestContext.currentUser()?.id,
                "error" to requestContext.getMessage(key),
            )
        }
    }

    private fun validate(form: ProductAttributeForm) {
        if ("liretama_url".equals(form.name) && !form.value.isNullOrEmpty()) {
            try {
                URL(form.value)
            } catch (ex: IOException) {
                throw ConflictException(
                    error = Error(
                        code = ErrorCode.PRODUCT_LIRETAMA_URL_NOT_VALID,
                        parameter = Parameter(name = form.name, value = form.value)
                    )
                )
            }
        }
    }

    @GetMapping("/me/store/products/{id}/publish")
    fun publish(@PathVariable id: Long, model: Model): String {
        try {
            productService.publish(id)
            return "redirect:/me/store/products/$id"
        } catch (ex: Exception) {
            LOGGER.error("Unexpected error", ex)
            val error = toErrorKey(ex)
            return "redirect:/me/store/products/$id?error=$error"
        }
    }
}
