package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.model.OfferModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DecimalFormat

@Service
class ProductMapper(
    private val imageKit: ImageService,
    private val discountMapper: DiscountMapper,
    @Value("\${wutsi.image.product.thumbnail.width}") private val thumbnailWidth: Int,
    @Value("\${wutsi.image.product.thumbnail.height}") private val thumbnailHeight: Int,
    @Value("\${wutsi.image.product.image.width}") private val imageWidth: Int,
    @Value("\${wutsi.image.product.image.height}") private val imageHeight: Int,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    fun toProductModel(product: ProductSummary, offer: Offer?) = ProductModel(
        id = product.id,
        title = product.title,
        imageUrl = generateImageUrl(product.imageUrl),
        thumbnailUrl = generateThumbnailUrl(product.imageUrl),
        fileUrl = product.fileUrl,
        price = toMoneyModel(product.price, product.currency),
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
        category = product.categoryId?.let { CategoryModel(it) }
    )

    fun toProductModel(product: Product, offer: Offer?) = ProductModel(
        id = product.id,
        title = product.title,
        imageUrl = generateImageUrl(product.imageUrl),
        thumbnailUrl = generateThumbnailUrl(product.imageUrl),
        fileUrl = product.fileUrl,
        price = toMoneyModel(product.price, product.currency),
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
        category = product.category?.let { toCategoryModel(it) }
    )

    private fun toCategoryModel(category: Category): CategoryModel {
        val language = LocaleContextHolder.getLocale().language
        return CategoryModel(
            id = category.id,
            level = category.level,
            parentId = category.parentId,
            longTitle = if (language == "en") category.longTitle else (category.longTitleFrench ?: category.longTitle),
            title = if (language == "en") category.title else (category.titleFrench ?: category.title),
        )
    }

    private fun toOfferModel(offer: Offer?, productId: Long, price: Long, currency: String): OfferModel =
        offer?.let {
            OfferModel(
                productId = offer.productId,
                referencePrice = toMoneyModel(offer.referencePrice, currency),
                price = toMoneyModel(offer.price, currency),
                discount = offer.discount?.let { discount -> discountMapper.toDiscountModel(discount) },
                savingPercentage = offer.savingPercentage,
                savingAmount = toMoneyModel(offer.savingAmount, currency)
            )
        }
            ?: OfferModel(
                productId = productId,
                referencePrice = toMoneyModel(price, currency),
                price = toMoneyModel(price, currency)
            )

    fun toMoneyModel(amount: Long, currency: String) = MoneyModel(
        value = amount,
        currency = currency,
        text = formatMoney(amount, currency)
    )

    private fun formatMoney(amount: Long, currency: String): String {
        val country = Country.all.find { currency.equals(it.currency, true) }
        return if (country != null) {
            DecimalFormat(country.monetaryFormat).format(amount)
        } else {
            "$amount $currency"
        }
    }

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
