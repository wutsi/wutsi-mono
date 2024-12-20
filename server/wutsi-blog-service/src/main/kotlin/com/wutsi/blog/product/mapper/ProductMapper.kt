package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.util.StringUtils
import org.springframework.stereotype.Service

@Service
class ProductMapper(
    private val categoryMapper: CategoryMapper
) {
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
        previewUrl = product.previewUrl,
        imageUrl = product.imageUrl,
        fileContentType = product.fileContentType,
        fileContentLength = product.fileContentLength,
        slug = toSlug(product),
        viewCount = product.viewCount,
        category = product.category?.let { categoryMapper.toCategory(it) },
        language = product.language,
        numberOfPages = product.numberOfPages,
        type = product.type,
        liretamaUrl = product.liretamaUrl,
        processingFile = product.processingFile,
        processingFileDateTime = product.processingFileDateTime,
        hashtag = product.hashtag,
        cvr = product.cvr,
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
        categoryId = product.category?.id,
        type = product.type,
        liretamaUrl = product.liretamaUrl,
        cvr = product.cvr,
    )

    fun toSlug(product: ProductEntity): String =
        StringUtils.toSlug("/product/${product.id}", product.title)

    fun toHashtag(hashtag: String?): String? =
        hashtag?.let {
            StringUtils.toSlug("", hashtag).lowercase().replace("/", "").lowercase()
        }
}
