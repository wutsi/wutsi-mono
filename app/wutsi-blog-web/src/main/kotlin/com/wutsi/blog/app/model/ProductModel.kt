package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import org.apache.commons.lang3.StringUtils
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale

data class ProductModel(
    val id: Long = -1,
    val storeId: String = "",
    val title: String = "",
    val description: String? = null,
    val price: MoneyModel = MoneyModel(),
    val available: Boolean = true,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val fileUrl: String? = null,
    val slug: String = "",
    val url: String = "",
    val orderCount: Long = 0,
    val totalSales: Long = 0,
    val fileContentLength: Long = 0,
    val fileContentType: String? = null,
    val externalId: String? = null,
    val viewCount: Long = 0,
    val offer: OfferModel = OfferModel(),
    val category: CategoryModel? = null,
    val language: String? = null,
    val numberOfPages: Int? = null,
    val type: ProductType = ProductType.UNKNOWN,
    val status: ProductStatus = ProductStatus.DRAFT,
    val liretamaUrl: String? = null,
) {
    companion object {
        const val ONE_DAY_MILLIS = 86400000L
        const val URGENCY_DAYS = 2
    }

    val fileExtension
        get() = when (fileContentType) {
            "text/plain" -> "txt"
            "application/pdf" -> "pdf"
            "application/epub+zip" -> "epub"
            "application/gzip" -> "gz"
            "application/msword" -> "doc"
            else -> "bin"
        }
    val fileContentLengthText: String
        get() = NumberUtils.toHumanReadable(fileContentLength, suffix = "b")

    val orderCountText: String
        get() = NumberUtils.toHumanReadable(orderCount)

    val viewCountText: String
        get() = NumberUtils.toHumanReadable(viewCount)

    val displayLanguage: String?
        get() = language?.let {
            StringUtils.capitalize(
                Locale(language).getDisplayLanguage(LocaleContextHolder.getLocale())
            )
        }

    val discountExpiryDays: Long?
        get() = offer.discount?.expiryDate?.let { expiryDate ->
            (expiryDate.time - System.currentTimeMillis()) / ONE_DAY_MILLIS
        }

    val showDiscountExpiryDate: Boolean
        get() = discountExpiryDays?.let { days -> days >= 0 && days <= URGENCY_DAYS } ?: false

    val published: Boolean
        get() = status == ProductStatus.PUBLISHED

    val draft: Boolean
        get() = status == ProductStatus.DRAFT

    val streamable: Boolean
        get() = (fileContentType == "application/epub+zip")
}
