package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.PriceModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.DecimalFormat

@Service
class ProductMapper(
    private val imageKit: ImageService,
    @Value("\${wutsi.image.product.thumbnail.width}") private val thumbnailWidth: Int,
    @Value("\${wutsi.image.product.thumbnail.height}") private val thumbnailHeight: Int,
    @Value("\${wutsi.image.product.image.width}") private val imageWidth: Int,
    @Value("\${wutsi.image.product.image.height}") private val imageHeight: Int,
    @Value("\${wutsi.application.server-url}") private val serverUrl: String,
) {
    fun toProductModel(product: ProductSummary, store: StoreModel) = ProductModel(
        id = product.id,
        title = product.title,
        imageUrl = generateImageUrl(product.imageUrl),
        thumbnailUrl = generateThumbnailUrl(product.imageUrl),
        fileUrl = product.fileUrl,
        store = store,
        price = toPriceModel(product.price, store),
        slug = product.slug,
        url = "$serverUrl${product.slug}",
        available = product.available
    )

    fun toProductModel(product: Product, store: StoreModel) = ProductModel(
        id = product.id,
        title = product.title,
        imageUrl = generateImageUrl(product.imageUrl),
        thumbnailUrl = generateThumbnailUrl(product.imageUrl),
        fileUrl = product.fileUrl,
        store = store,
        price = toPriceModel(product.price, store),
        slug = product.slug,
        url = "$serverUrl${product.slug}",
        description = product.description,
        available = product.available,
        fileContentLength = product.fileContentLength,
        fileContentType = product.fileContentType,
        totalSales = product.totalSales,
        orderCount = product.orderCount,
    )

    fun toPriceModel(amount: Long, store: StoreModel) = PriceModel(
        amount = amount,
        currency = store.currency,
        priceText = formatMoney(amount, store)
    )

    private fun formatMoney(amount: Long, store: StoreModel): String {
        val country = Country.all.find { store.currency.equals(it.currency, true) }
        return if (country != null) {
            DecimalFormat(country.monetaryFormat).format(amount)
        } else {
            "$amount ${store.currency}"
        }
    }

    private fun generateThumbnailUrl(url: String?): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        return imageKit.transform(
            url = url,
            transformation = Transformation(
                Dimension(width = thumbnailWidth, height = thumbnailHeight),
                focus = Focus.TOP,
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
                Dimension(width = imageWidth, height = imageHeight),
                focus = Focus.TOP,
            ),
        )
    }
}
