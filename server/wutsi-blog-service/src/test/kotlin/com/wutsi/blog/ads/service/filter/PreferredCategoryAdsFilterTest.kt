package com.wutsi.blog.ads.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.product.domain.CategoryEntity
import com.wutsi.blog.user.dao.PreferredCategoryRepository
import com.wutsi.blog.user.domain.PreferredCategoryEntity
import com.wutsi.blog.user.domain.UserEntity
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class PreferredCategoryAdsFilterTest {
    private val preferredCategoryDao = mock<PreferredCategoryRepository>()
    private val filter = PreferredCategoryAdsFilter(preferredCategoryDao)

    private val ads = listOf(
        AdsEntity(id = "10", type = AdsType.BANNER_WEB, category = CategoryEntity(id = 100)),
        AdsEntity(id = "11", type = AdsType.BANNER_MOBILE),
        AdsEntity(id = "12", type = AdsType.BANNER_WEB, category = CategoryEntity(id = 100)),
        AdsEntity(
            id = "13",
            type = AdsType.BOX,
            category = CategoryEntity(id = 110, parent = CategoryEntity(100))
        ),
        AdsEntity(id = "14", type = AdsType.BANNER_WEB, category = CategoryEntity(id = 200)),
    )

    @Test
    fun `no user`() {
        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(categoryId = 100)),
            ads,
            null
        )

        assertEquals(ads.size, result.size)
        assertEquals(ads.map { it.id }, result.map { it.id })
    }

    @Test
    fun `no preferred category`() {
        val user = UserEntity(id = 1)
        doReturn(emptyList<PreferredCategoryEntity?>())
            .whenever(preferredCategoryDao)
            .findByUserIdOrderByTotalReadsDesc(any())

        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(categoryId = 100)),
            ads,
            user
        )

        assertEquals(ads.size, result.size)
        assertEquals(ads.map { it.id }, result.map { it.id })
    }

    @Test
    fun `with preferred category`() {
        val user = UserEntity(id = 1)
        doReturn(
            listOf(
                PreferredCategoryEntity(categoryId = 200),
                PreferredCategoryEntity(categoryId = 100)
            )
        )
            .whenever(preferredCategoryDao)
            .findByUserIdOrderByTotalReadsDesc(any())

        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(categoryId = 100)),
            ads,
            user
        )

        assertEquals(ads.size, result.size)
        assertEquals(listOf("14", "10", "12", "11", "13"), result.map { it.id })
    }
}
