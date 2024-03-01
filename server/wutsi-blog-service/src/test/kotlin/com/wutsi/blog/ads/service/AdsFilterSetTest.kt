package com.wutsi.blog.ads.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AdsFilterSetTest {
    @Test
    fun filter() {
        // GIVEN
        val ads1 = AdsEntity(id = "1")
        val ads2 = AdsEntity(id = "2")
        val ads3 = AdsEntity(id = "3")
        val ads4 = AdsEntity(id = "4")
        val ads5 = AdsEntity(id = "5")
        val ads6 = AdsEntity(id = "6")
        val ads7 = AdsEntity(id = "7")

        val filter1 = mock<AdsFilter>()
        doReturn(listOf(ads2, ads3, ads4, ads5, ads6)).whenever(filter1).filter(any(), any())

        val filter2 = mock<AdsFilter>()
        doReturn(listOf(ads3, ads4, ads5)).whenever(filter2).filter(any(), any())

        // WHEN
        val request = mock<SearchAdsRequest> { }
        val set = AdsFilterSet(listOf(filter1, filter2))
        val result = set.filter(request, listOf(ads1, ads2, ads3, ads4, ads5, ads6, ads7))

        // THEN
        assertEquals(3, result.size)
        assertEquals(listOf(ads3, ads4, ads5), result)
    }
}