package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.user.dao.PreferredCategoryRepository
import com.wutsi.blog.user.domain.PreferredCategoryEntity
import com.wutsi.blog.user.domain.UserEntity

class PreferredCategoryAdsFilter(
    private val preferredCategoryDao: PreferredCategoryRepository,
) : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        user?.id ?: return ads

        val categories: List<PreferredCategoryEntity?> = preferredCategoryDao.findByUserIdOrderByTotalReadsDesc(user.id)
        val categoryIds = categories
            .map { category -> category?.categoryId }
            .filterNotNull()
        if (categoryIds.isEmpty()) {
            return ads
        }

        val result = mutableListOf<AdsEntity>()
        categoryIds.forEach { categoryId ->
            result.addAll(
                ads.filter { ads -> ads.category?.id == categoryId }
            )
        }
        result.addAll(
            ads.filter { ad ->
                ad.category == null || !categoryIds.contains(ad.category?.id)
            }
        )
        return result
    }
}
