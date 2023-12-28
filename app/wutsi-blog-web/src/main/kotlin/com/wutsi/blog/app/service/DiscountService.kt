package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.DiscountBackend
import com.wutsi.blog.app.mapper.DiscountMapper
import com.wutsi.blog.app.model.DiscountModel
import com.wutsi.blog.product.dto.SearchDiscountRequest
import org.springframework.stereotype.Service

@Service
class DiscountService(
    private val backend: DiscountBackend,
    private val mapper: DiscountMapper,
) {
    fun search(request: SearchDiscountRequest): List<DiscountModel> =
        backend.search(request).discounts.map { mapper.toDiscountModel(it) }
}
