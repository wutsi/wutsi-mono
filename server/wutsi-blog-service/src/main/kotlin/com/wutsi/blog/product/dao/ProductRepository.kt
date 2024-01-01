package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long> {
    fun findByStoreAndStatusAndExternalIdNotIn(
        store: StoreEntity,
        status: ProductStatus,
        externalIds: List<String>
    ): List<ProductEntity>

    fun findByExternalIdAndStore(externalId: String, store: StoreEntity): List<ProductEntity>

    fun countByStore(store: StoreEntity): Long?

    fun countByStoreAndStatus(store: StoreEntity, status: ProductStatus): Long?
}
