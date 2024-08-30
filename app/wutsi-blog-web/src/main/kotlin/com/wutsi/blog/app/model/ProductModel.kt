package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.platform.core.storage.MimeTypes
import org.apache.commons.lang3.StringUtils
import org.springframework.context.i18n.LocaleContextHolder
import java.text.DecimalFormat
import java.util.Locale

data class ProductModel(
    val id: Long = -1,
    val storeId: String = "",
    val title: String = "",
    val description: String? = null,
    val price: MoneyModel = MoneyModel(),
    val available: Boolean = true,
    val originalImageUrl: String? = null,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val fileUrl: String? = null,
    val slug: String = "",
    val url: String = "",
    val orderCount: Long = 0,
    val totalSales: MoneyModel = MoneyModel(),
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
    val processingFile: Boolean = false,
    val hashtag: String? = null,
    val cvr: Double = 0.0,
) {
    companion object {
        const val ONE_DAY_MILLIS = 86400000L
        const val URGENCY_DAYS = 2
        private val MIME_TYPES = MimeTypes()
    }

    val titleJS
        get() = title.replace("'", "\\'")

    val fileExtension
        get() = fileUrl?.let { MIME_TYPES.extension(fileUrl) } ?: "bin"

    val fileName
        get() = fileUrl?.let {
            val i = it.lastIndexOf("/")
            if (i > 0) it.substring(i + 1) else it
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
        get() = epub || cbz || (zip && comics)

    val epub: Boolean
        get() = fileContentType == MimeTypes.EPUB

    val cbz: Boolean
        get() = fileContentType == MimeTypes.CBZ

    val pdf: Boolean
        get() = fileContentType == MimeTypes.CBZ

    val zip: Boolean
        get() = fileContentType == MimeTypes.ZIP

    val ebook: Boolean
        get() = ProductType.EBOOK == type

    val comics: Boolean
        get() = ProductType.COMICS == type

    val fileEmpty: Boolean
        get() = fileContentLength == 0L

    val cvrText: String
        get() = DecimalFormat("0.00").format(100.0 * cvr) + "%"

    val totalSalesText: String
        get() = NumberUtils.toHumanReadable(totalSales.value)
}
