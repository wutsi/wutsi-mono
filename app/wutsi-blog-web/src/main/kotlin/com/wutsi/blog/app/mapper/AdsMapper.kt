package com.wutsi.blog.app.mapper

import com.wutsi.blog.ads.dto.Ads
import com.wutsi.blog.ads.dto.AdsSummary
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.app.model.CategoryModel
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class AdsMapper(
    private val imageKit: ImageService,
    private val moneyMapper: MoneyMapper,
    @Value("\${wutsi.application.asset-url}") private val assertUrl: String,
) {
    fun toAdsModel(ads: Ads, category: CategoryModel?): AdsModel {
        val fmt = createDateFormater()
        val fmtYYYMMMDD = createDateFormaterYYYMMMDDD()
        return AdsModel(
            id = ads.id,
            userId = ads.userId,
            creationDateTime = ads.creationDateTime,
            modificationDateTime = ads.modificationDateTime,
            completedDateTime = ads.completedDateTime,
            startDate = ads.startDate,
            endDate = ads.endDate,
            startDateText = ads.startDate?.let { date -> fmt.format(date) },
            startDateYYYYMMDD = ads.startDate?.let { date -> fmtYYYMMMDD.format(date) },
            endDateText = ads.endDate?.let { date -> fmt.format(date) },
            endDateYYYYMMDD = ads.endDate?.let { date -> fmtYYYMMMDD.format(date) },
            maxImpressions = ads.maxImpressions,
            budget = moneyMapper.toMoneyModel(ads.budget, ads.currency),
            dailyBudget = moneyMapper.toMoneyModel(ads.dailyBudget, ads.currency),
            durationDays = ads.durationDays,
            maxDailyImpressions = ads.maxDailyImpressions,
            status = ads.status,
            url = ads.url?.ifEmpty { null },
            totalClicks = ads.totalClicks,
            imageUrl = ads.imageUrl ?: "$assertUrl/assets/wutsi/img/no-image.png",
            thumbnailUrl = generateThumbnailUrl(ads.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
            totalImpressions = ads.totalImpressions,
            title = ads.title,
            type = ads.type,
            ctaType = ads.ctaType,
            country = ads.country,
            language = ads.language,
            gender = ads.gender,
            os = ads.os,
            email = ads.email,
            transactionId = ads.transactionId,
            category = category,
            orderCount = ads.orderCount,
            totalSales = moneyMapper.toMoneyModel(ads.totalSales, ads.currency),
        )
    }

    fun toAdsModel(ads: AdsSummary, category: CategoryModel?): AdsModel {
        val fmt = createDateFormater()
        val fmtYYYMMMDD = createDateFormaterYYYMMMDDD()
        return AdsModel(
            id = ads.id,
            creationDateTime = ads.creationDateTime,
            modificationDateTime = ads.modificationDateTime,
            startDate = ads.startDate,
            startDateText = ads.startDate?.let { date -> fmt.format(date) },
            startDateYYYYMMDD = ads.startDate?.let { date -> fmtYYYMMMDD.format(date) },
            endDate = ads.endDate,
            endDateText = ads.endDate?.let { date -> fmt.format(date) },
            endDateYYYYMMDD = ads.endDate?.let { date -> fmtYYYMMMDD.format(date) },
            status = ads.status,
            totalClicks = ads.totalClicks,
            imageUrl = ads.imageUrl ?: "$assertUrl/assets/wutsi/img/no-image.png",
            thumbnailUrl = generateThumbnailUrl(ads.imageUrl) ?: "$assertUrl/assets/wutsi/img/no-image.png",
            totalImpressions = ads.totalImpressions,
            title = ads.title,
            budget = moneyMapper.toMoneyModel(ads.budget, ads.currency),
            dailyBudget = moneyMapper.toMoneyModel(ads.dailyBudget, ads.currency),
            durationDays = ads.durationDays,
            type = ads.type,
            url = ads.url?.ifEmpty { null },
            ctaType = ads.ctaType,
            transactionId = ads.transactionId,
            category = category,
            orderCount = ads.orderCount,
            totalSales = moneyMapper.toMoneyModel(ads.totalSales, ads.currency),
        )
    }

    private fun generateThumbnailUrl(url: String?): String? {
        if (url.isNullOrEmpty()) {
            return null
        }

        return imageKit.transform(
            url = url,
            transformation = Transformation(
                Dimension(width = 256),
            ),
        )
    }

    private fun createDateFormater() = SimpleDateFormat("dd MMM yyyy", LocaleContextHolder.getLocale())
    private fun createDateFormaterYYYMMMDDD() = SimpleDateFormat("yyyy-MM-dd", LocaleContextHolder.getLocale())
}
