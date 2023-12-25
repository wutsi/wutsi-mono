package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.util.SlugGenerator
import org.springframework.stereotype.Service

@Service
class ProductMapper {
    fun toProduct(product: ProductEntity) = Product(
        id = product.id ?: -1,
        storeId = product.store.id ?: "",
        currency = product.store.currency,
        totalSales = product.totalSales,
        orderCount = product.orderCount,
        externalId = product.externalId,
        price = product.price,
        status = product.status,
        creationDateTime = product.creationDateTime,
        description = product.description,
        title = product.title,
        modificationDateTime = product.modificationDateTime,
        available = product.available,
        fileUrl = product.fileUrl,
        imageUrl = product.imageUrl,
        fileContentType = product.fileContentType,
        fileContentLength = product.fileContentLength,
        slug = toSlug(product),
        viewCount = product.viewCount,
    )

    fun toProductSummary(product: ProductEntity) = ProductSummary(
        id = product.id ?: -1,
        storeId = product.store.id ?: "",
        externalId = product.externalId,
        currency = product.store.currency,
        price = product.price,
        status = product.status,
        title = product.title,
        available = product.available,
        fileUrl = product.fileUrl,
        imageUrl = product.imageUrl,
        slug = toSlug(product),
        orderCount = product.orderCount,
        totalSales = product.totalSales,
        fileContentType = product.fileContentType,
        fileContentLength = product.fileContentLength,
        viewCount = product.viewCount,
    )

    fun toSlug(product: ProductEntity): String =
        SlugGenerator.generate("/product/${product.id}", product.title)
}
