package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.PageEntity
import com.wutsi.blog.product.domain.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PageRepository : CrudRepository<PageEntity, Long> {
    fun findByProduct(product: ProductEntity): List<PageEntity>

    fun findByProductAndNumber(product: ProductEntity, number: Int): List<PageEntity>
}
