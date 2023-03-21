package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.GetDiscountResponse
import com.wutsi.marketplace.access.service.DiscountService
import org.springframework.stereotype.Service

@Service
public class GetDiscountDelegate(private val service: DiscountService) {
    public fun invoke(id: Long): GetDiscountResponse {
        val discount = service.findById(id)
        return GetDiscountResponse(
            discount = service.toDiscount(discount),
        )
    }
}
