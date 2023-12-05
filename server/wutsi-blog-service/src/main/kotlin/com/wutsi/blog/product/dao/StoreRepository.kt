package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.StoreEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StoreRepository : CrudRepository<StoreEntity, String> {
    fun findByUserId(userId: Long): Optional<StoreEntity>
}
