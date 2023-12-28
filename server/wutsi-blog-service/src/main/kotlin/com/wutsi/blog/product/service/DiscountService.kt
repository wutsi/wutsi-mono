package com.wutsi.blog.product.service

import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.SearchDiscountRequest
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class DiscountService(
    private val userDao: UserRepository,
    private val storeDao: StoreRepository,
    private val ruleSet: DiscountRuleSet,
) {
    fun search(request: SearchDiscountRequest): List<Discount> {
        val user = userDao.findById(request.userId).getOrNull() ?: return emptyList()
        val store = storeDao.findById(request.storeId).getOrNull() ?: return emptyList()

        return search(store, user)
    }

    fun search(store: StoreEntity, user: UserEntity): List<Discount> =
        ruleSet.findDiscounts(store, user)
}
