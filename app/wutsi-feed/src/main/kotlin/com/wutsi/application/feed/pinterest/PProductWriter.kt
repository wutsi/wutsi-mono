package com.wutsi.application.feed.pinterest

import com.wutsi.application.feed.model.ProductModel
import com.wutsi.application.feed.service.AbstractProductWriter
import org.springframework.stereotype.Service

/**
 * See https://help.pinterest.com/en/business/article/before-you-get-started-with-catalogs
 */
@Service
class PProductWriter : AbstractProductWriter() {
    override fun toHeaders() = arrayOf(
        "id",
        "title",
        "description",
        "availability",
        "condition",
        "price",
        "sale_price",
        "brand",
        "google_product_category",
        "link",
        "image_link",
        "additional_image_link",
    )

    override fun toRecord(item: ProductModel) = arrayOf(
        item.id,
        item.title,
        item.description,
        item.availability,
        item.condition,
        item.price,
        item.salePrice,
        item.brand,
        item.googleProductCategory?.toString(),
        item.link,
        item.imageLink,
        item.additionalImageLink.joinToString(separator = ","),
    )
}
