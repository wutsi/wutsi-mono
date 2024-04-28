package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsEmailFilterTest {
    private val filter = AdsEmailFilter()

    private val ads = listOf(
        AdsEntity(id = "10", type = AdsType.BANNER_WEB, todayImpressions = 5, email = true),
        AdsEntity(id = "11", type = AdsType.BANNER_MOBILE, todayImpressions = 10, email = false),
        AdsEntity(id = "12", type = AdsType.BOX, todayImpressions = 20),
    )

    @Test
    fun email() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                email = true
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("10", result[0].id)
        assertEquals("12", result[1].id)
    }

    @Test
    fun noEmail() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                email = null
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(3, result.size)
        assertEquals("10", result[0].id)
        assertEquals("11", result[0].id)
        assertEquals("12", result[1].id)
    }
}