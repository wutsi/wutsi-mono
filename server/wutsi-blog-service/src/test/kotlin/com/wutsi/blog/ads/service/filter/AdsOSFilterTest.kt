package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.OS
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsOSFilterTest {
    private val filter = AdsOSFilter()

    private val ads = listOf(
        AdsEntity(id = "10", os = OS.ANDROID),
        AdsEntity(id = "11", os = OS.IOS),
        AdsEntity(id = "12"),
        AdsEntity(id = "13", os = OS.WINDOWS),
    )

    @Test
    fun iphone() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4.1 Mobile/15E148 Safari/604.1"
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("11", result[0].id)
        assertEquals("12", result[1].id)
    }

    @Test
    fun ipad() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (iPad; CPU OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1 musical_ly_34.0.0 JsSdk/2.0 NetType/WIFI Channel/App Store ByteLocale/en Region/IE isDarkMode/0 WKWebView/1 RevealType/Dialog"
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("11", result[0].id)
        assertEquals("12", result[1].id)
    }

    @Test
    fun windows() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)"
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("12", result[0].id)
        assertEquals("13", result[1].id)
    }

    @Test
    fun android() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36"
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(2, result.size)
        assertEquals("10", result[0].id)
        assertEquals("12", result[1].id)
    }

    @Test
    fun noUA() {
        val request = SearchAdsRequest(
            impressionContext = AdsImpressionContext(
                userAgent = null
            )
        )
        val result = filter.filter(request, ads, null)
        assertEquals(4, result.size)
    }
}