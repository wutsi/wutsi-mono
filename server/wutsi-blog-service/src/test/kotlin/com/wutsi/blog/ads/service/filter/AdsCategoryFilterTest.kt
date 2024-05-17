package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.product.domain.CategoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsCategoryFilterTest {
    private val filter = AdsCategoryFilter()

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
    fun filter() {
        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(categoryId = 100)),
            ads,
            null
        )

        assertEquals(4, result.size)
        assertEquals("10", result[0].id)
        assertEquals("11", result[1].id)
        assertEquals("12", result[2].id)
        assertEquals("13", result[3].id)
    }

    @Test
    fun noCategoryId() {
        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(categoryId = null)),
            ads,
            null
        )

        assertEquals(1, result.size)
        assertEquals("11", result[0].id)
    }
}