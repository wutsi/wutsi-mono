package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.ProductImportEntity
import com.wutsi.blog.product.domain.StoreEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductImportRepository : CrudRepository<ProductImportEntity, Long> {
    fun findByStore(store: StoreEntity): List<ProductImportEntity>
}
