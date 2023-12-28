package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.DiscountModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.product.dto.Discount
import org.springframework.stereotype.Service

@Service
class DiscountMapper(private val moment: Moment) {
    fun toDiscountModel(discount: Discount) = DiscountModel(
        type = discount.type,
        expiryDate = discount.expiryDate,
        percentage = discount.percentage,
        expiryDateText = moment.format(discount.expiryDate),
    )
}
