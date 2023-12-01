package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Long> {
    fun findByExternalIdAndUserId(externalId: String, userId: Long): Optional<ProductEntity>

    fun countByUserId(userId: Long): Long?
}
