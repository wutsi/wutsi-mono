package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsDeviceTypeFilterTest {
    private val filter = AdsDeviceTypeFilter()

    private val ads = listOf(
        AdsEntity(id = "10", type = AdsType.BANNER_WEB, todayImpressions = 5),
        AdsEntity(id = "11", type = AdsType.BANNER_MOBILE, todayImpressions = 10),
        AdsEntity(id = "12", type = AdsType.BOX, todayImpressions = 20),
        AdsEntity(id = "13", type = AdsType.BOX_2X, todayImpressions = 20),
        AdsEntity(id = "20", type = AdsType.BANNER_WEB, todayImpressions = 10),
        AdsEntity(id = "21", type = AdsType.BANNER_MOBILE, todayImpressions = 0),
        AdsEntity(id = "30", type = AdsType.BANNER_WEB, todayImpressions = 0),
        AdsEntity(id = "32", type = AdsType.BOX, todayImpressions = 10),
        AdsEntity(id = "40", type = AdsType.BANNER_WEB, todayImpressions = 100),
        AdsEntity(id = "42", type = AdsType.BOX, todayImpressions = 0),
        AdsEntity(id = "50", type = AdsType.BANNER_WEB, todayImpressions = 0),
    )

    @Test
    fun desktop() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36"
            )
        )
        val result = filter.filter(request, ads)
        assertEquals(9, result.size)
        assertEquals("10", result[0].id)
        assertEquals("12", result[1].id)
        assertEquals("13", result[2].id)
        assertEquals("20", result[3].id)
        assertEquals("30", result[4].id)
        assertEquals("32", result[5].id)
        assertEquals("40", result[6].id)
        assertEquals("42", result[7].id)
        assertEquals("50", result[8].id)
    }

    @Test
    fun mobile() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)"
            )
        )
        val result = filter.filter(request, ads)
        assertEquals(6, result.size)
        assertEquals("11", result[0].id)
        assertEquals("12", result[1].id)
        assertEquals("13", result[2].id)
        assertEquals("21", result[3].id)
        assertEquals("32", result[4].id)
        assertEquals("42", result[5].id)
    }
}