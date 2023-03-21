package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.service.DiscountService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class AddDiscountProductDelegate(private val service: DiscountService) {
    @Transactional
    public fun invoke(discountId: Long, productId: Long) {
        service.addProduct(discountId, productId)
    }
}
