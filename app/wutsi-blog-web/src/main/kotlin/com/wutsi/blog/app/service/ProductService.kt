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
) {
    fun import(cmd: ImportProductCommand) {
        backend.import(cmd)
    }

    fun search(request: SearchProductRequest): List<ProductModel> {
        return backend.search(request)
            .products
            .map { mapper.toProductModel(it) }
    }

    fun get(id: Long): ProductModel {
        val product = backend.get(id).product
        return mapper.toProductModel(product)
    }
}
