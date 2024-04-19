package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.SearchProductRequest

interface ProductSearchFilter {
    fun filter(request: SearchProductRequest, products: List<ProductEntity>): List<ProductEntity>
}
