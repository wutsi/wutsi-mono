package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.DiscountEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DiscountRepository : CrudRepository<DiscountEntity, Long>
