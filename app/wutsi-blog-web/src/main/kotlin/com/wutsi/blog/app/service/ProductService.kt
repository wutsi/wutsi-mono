package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.ProductBackend
import com.wutsi.blog.app.mapper.ProductMapper
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val backend: ProductBackend,
    private val storeService: StoreService,
    private val mapper: ProductMapper,
    private val requestContext: RequestContext,
) {
    fun import(cmd: ImportProductCommand) {
        backend.import(cmd)
    }

    fun search(request: SearchProductRequest, store: StoreModel): List<ProductModel> {
        return backend.search(request)
            .products
            .map { mapper.toProductModel(it, store) }
    }

    fun get(id: Long): ProductModel {
        val product = backend.get(id).product
        val store = storeService.get(product.storeId)
        return mapper.toProductModel(product, store)
    }
}
