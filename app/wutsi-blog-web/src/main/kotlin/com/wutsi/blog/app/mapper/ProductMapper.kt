package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.app.model.OfferModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ProductMapper(
    private val imageKit: ImageService,
    private val discountMapper: DiscountMapper,
    private val categoryMapper: CategoryMapper,
    private val moneyMapper: MoneyMapper,
    @Value("\${wutsi.image.product.thumbnail.width}") private val thumbnailWidth: Int,
    @Value("\${wutsi.image.product.thumbnail.height}") private val thumbnailHeight: Int,
    @Value("\${wutsi.image.product.image.width}") private val imageWidth: Int,
    @Value("\${wutsi.image.product.image.height}") private val imageHeight: Int,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
    @Value("\${wutsi.application.asset-url}") private val assertUrl: String,
) {
    fun toProductModel(product: ProductSummary, offer: Offer?, category: CategoryModel?) = ProductModel(
        id = product.id,
        title = product.title,
        originalImageUrl = product.imageUrl,
        imageUrl = generateImageUrl(product.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
        thumbnailUrl = generateThumbnailUrl(product.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
        fileUrl = product.fileUrl,
        price = moneyMapper.toMoneyModel(product.price, product.currency),
        slug = product.slug,
        url = "$serverUrl${product.slug}",
        available = product.available,
        totalSales = product.totalSales,
        orderCount = product.orderCount,
        storeId = product.storeId,
        fileContentLength = product.fileContentLength,
        fileContentType = product.fileContentType,
        externalId = product.externalId,
        viewCount = product.viewCount,
        offer = toOfferModel(offer, product.id, product.price, product.currency),
        category = category,
        type = product.type,
        status = product.status,
        liretamaUrl = product.liretamaUrl,
    )

    fun toProductModel(product: Product, offer: Offer?) = ProductModel(
        id = product.id,
        title = product.title,
        originalImageUrl = product.imageUrl,
        imageUrl = generateImageUrl(product.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
        thumbnailUrl = generateThumbnailUrl(product.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
        fileUrl = product.fileUrl,
        price = moneyMapper.toMoneyModel(product.price, product.currency),
        slug = product.slug,
        url = "$serverUrl${product.slug}",
        description = product.description?.ifEmpty { null },
        available = product.available,
        fileContentLength = product.fileContentLength,
        fileContentType = product.fileContentType,
        totalSales = product.totalSales,
        orderCount = product.orderCount,
        storeId = product.storeId,
        externalId = product.externalId,
        viewCount = product.viewCount,
        offer = toOfferModel(offer, product.id, product.price, product.currency),
        category = product.category?.let { categoryMapper.toCategoryModel(it) },
        numberOfPages = product.numberOfPages,
        language = product.language,
        type = product.type,
        status = product.status,
        liretamaUrl = product.liretamaUrl,
    )

    private fun toOfferModel(offer: Offer?, productId: Long, price: Long, currency: String): OfferModel =
        offer?.let {
            OfferModel(
                productId = offer.productId,
                referencePrice = moneyMapper.toMoneyModel(offer.referencePrice, currency),
                price = moneyMapper.toMoneyModel(offer.price, currency),
                discount = offer.discount?.let { discount -> discountMapper.toDiscountModel(discount) },
                savingPercentage = offer.savingPercentage,
                savingAmount = moneyMapper.toMoneyModel(offer.savingAmount, currency),
                internationalPrice = offer.internationalPrice?.let { price ->
                    moneyMapper.toMoneyModel(price, offer.internationalCurrency!!)
                }
            )
        }
            ?: OfferModel(
                productId = productId,
                referencePrice = moneyMapper.toMoneyModel(price, currency),
                price = moneyMapper.toMoneyModel(price, currency)
            )

    private fun generateThumbnailUrl(url: String?): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        return imageKit.transform(
            url = url,
            transformation = Transformation(
                Dimension(width = thumbnailWidth),
            ),
        )
    }

    private fun generateImageUrl(url: String?): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        return imageKit.transform(
            url = url,
            transformation = Transformation(
                Dimension(width = imageWidth),
            ),
        )
    }
}
