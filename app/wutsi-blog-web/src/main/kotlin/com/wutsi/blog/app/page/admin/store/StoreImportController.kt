package com.wutsi.blog.app.page.admin.store

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.ImportForm
import com.wutsi.blog.app.service.ProductService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ImportProductCommand
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/me/store/import")
class StoreImportController(
    private val productService: ProductService,
    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.STORE_IMPORT

    @GetMapping
    fun index(model: Model): String {
        return "admin/store/import"
    }

    @ResponseBody
    @PostMapping("/submit")
    fun submit(@RequestBody request: ImportForm): Map<String, String> {
        val store = requestContext.currentStore()
        if (store != null) {
            productService.import(
                ImportProductCommand(
                    url = request.url,
                    storeId = store.id
                )
            )
        }
        return mapOf()
    }
}
