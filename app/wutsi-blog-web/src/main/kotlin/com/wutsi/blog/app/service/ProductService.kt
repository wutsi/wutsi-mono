package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.ProductBackend
import com.wutsi.blog.app.mapper.ProductMapper
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val backend: ProductBackend,
    private val mapper: ProductMapper,
    private val requestContext: RequestContext,
) {
    fun import(cmd: ImportProductCommand) {
        backend.import(cmd)
    }

    fun search(request: SearchProductRequest): List<ProductModel> {
        val store = requestContext.currentStore() ?: return emptyList()
        return backend.search(request.copy(storeIds = listOf(store.id)))
            .products
            .map { mapper.toProductModel(it, store) }
    }
}
