package com.wutsi.blog.ads.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.app.backend.dto.IpApiResponse
import com.wutsi.blog.backend.IpApiBackend
import com.wutsi.blog.user.domain.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsCountryFilterTest {
    private val ipApi = mock<IpApiBackend>()
    private val filter = AdsCountryFilter(ipApi)

    private val ads = listOf(
        AdsEntity(id = "10", type = AdsType.BANNER_WEB, todayImpressions = 5, country = "cm"),
        AdsEntity(id = "11", type = AdsType.BANNER_MOBILE, todayImpressions = 10),
        AdsEntity(id = "12", type = AdsType.BANNER_WEB, todayImpressions = 100, country = "CM"),
        AdsEntity(id = "13", type = AdsType.BOX, todayImpressions = 0, country = "fr"),
    )

    @Test
    fun filter() {
        val result = filter.filter(SearchAdsRequest(), ads, UserEntity(country = "cm"))

        assertEquals(3, result.size)
        assertEquals("10", result[0].id)
        assertEquals("11", result[1].id)
        assertEquals("12", result[2].id)

        verify(ipApi, never()).resolve(any())
    }

    @Test
    fun noUser() {
        val result = filter.filter(SearchAdsRequest(), ads, null)

        assertEquals(1, result.size)
        assertEquals("11", result[0].id)

        verify(ipApi, never()).resolve(any())
    }

    @Test
    fun noUserWithIP() {
        val ip = "123.111.11.11"
        doReturn(IpApiResponse(countryCode = "fr")).whenever(ipApi).resolve(ip)

        val result = filter.filter(
            SearchAdsRequest(impressionContext = AdsImpressionContext(ip = "123.111.11.11")),
            ads,
            null
        )

        assertEquals(2, result.size)
        assertEquals("11", result[0].id)
        assertEquals("13", result[1].id)

        verify(ipApi, times(3)).resolve(ip)
    }
}