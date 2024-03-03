package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsImpressionFilterTest {
    private val filter = AdsImpressionFilter()

    private val request = SearchAdsRequest(
        impressionContext = AdsImpressionContext(
            adsPerType = 2
        )
    )

    private val ads = listOf(
        AdsEntity(id = "10", type = AdsType.BANNER_WEB, todayImpressions = 5),
        AdsEntity(id = "11", type = AdsType.BANNER_MOBILE, todayImpressions = 10),
        AdsEntity(id = "12", type = AdsType.BOX, todayImpressions = 20),
        AdsEntity(id = "20", type = AdsType.BANNER_WEB, todayImpressions = 10),
        AdsEntity(id = "21", type = AdsType.BANNER_MOBILE, todayImpressions = 0),
        AdsEntity(id = "30", type = AdsType.BANNER_WEB, todayImpressions = 0),
        AdsEntity(id = "32", type = AdsType.BOX, todayImpressions = 10),
        AdsEntity(id = "40", type = AdsType.BANNER_WEB, todayImpressions = 100),
        AdsEntity(id = "42", type = AdsType.BOX, todayImpressions = 0),
        AdsEntity(id = "50", type = AdsType.BANNER_WEB, todayImpressions = 0),
    )

    @Test
    fun filter() {
        val result = filter.filter(request, ads)
        assertEquals(6, result.size)
        assertEquals("30", result[0].id)
        assertEquals("50", result[1].id)
        assertEquals("21", result[2].id)
        assertEquals("11", result[3].id)
        assertEquals("42", result[4].id)
        assertEquals("32", result[5].id)
    }
}