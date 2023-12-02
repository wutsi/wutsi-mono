package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long> {
    fun findByExternalIdAndStore(externalId: String, store: StoreEntity): Optional<ProductEntity>

    fun countByStore(store: StoreEntity): Long?
}
