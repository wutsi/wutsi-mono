package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.user.domain.UserEntity

interface DiscountRule {
    fun apply(store: StoreEntity, user: UserEntity): Discount?
}
