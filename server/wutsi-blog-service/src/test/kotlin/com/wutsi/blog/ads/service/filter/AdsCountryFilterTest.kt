package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.user.domain.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsCountryFilterTest {
    private val filter = AdsCountryFilter()

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
    }

    @Test
    fun noUser() {
        val result = filter.filter(SearchAdsRequest(), ads, null)

        assertEquals(ads.size, result.size)
    }
}