package com.wutsi.marketplace.access.dao

import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.entity.ProductEntity
import com.wutsi.marketplace.access.entity.StoreEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long> {
    fun countByStoreAndIsDeleted(store: StoreEntity, isDeleted: Boolean): Int
    fun countByStoreAndIsDeletedAndStatus(
        store: StoreEntity,
        isDeleted: Boolean,
        status: ProductStatus,
    ): Int
}
