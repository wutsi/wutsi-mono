package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsBlogFilterTest {
    private val filter = AdsBlogFilter()

    private val ads = listOf(
        AdsEntity(id = "10", userId = 100),
        AdsEntity(id = "11", userId = 100),
        AdsEntity(id = "20", userId = 200),
        AdsEntity(id = "21", userId = 200),
    )

    @Test
    fun blog() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                blogId = 100
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("10", result[0].id)
        assertEquals("11", result[1].id)
    }

    @Test
    fun noBlog() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                blogId = null
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(4, result.size)
        assertEquals("10", result[0].id)
        assertEquals("11", result[1].id)
        assertEquals("20", result[2].id)
        assertEquals("21", result[3].id)
    }
}
