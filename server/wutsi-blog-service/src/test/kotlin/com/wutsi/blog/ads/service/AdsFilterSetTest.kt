package com.wutsi.blog.ads.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.platform.core.logging.KVLogger
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AdsFilterSetTest {
    private val logger = mock<KVLogger>()

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
        doReturn(listOf(ads2, ads3, ads4, ads5, ads6)).whenever(filter1).filter(any(), any(), anyOrNull())

        val filter2 = mock<AdsFilter>()
        doReturn(listOf(ads3, ads4, ads5)).whenever(filter2).filter(any(), any(), anyOrNull())

        // WHEN
        val request = SearchAdsRequest(impressionContext = AdsImpressionContext())
        val set = AdsFilterSet(listOf(filter1, filter2), logger)
        val result = set.filter(request, listOf(ads1, ads2, ads3, ads4, ads5, ads6, ads7), null)

        // THEN
        assertEquals(3, result.size)
        assertEquals(listOf(ads3, ads4, ads5), result)
    }

    @Test
    fun noImpressionContext() {
        // GIVEN
        val filter1 = mock<AdsFilter>()
        val filter2 = mock<AdsFilter>()

        // WHEN
        val request = SearchAdsRequest(impressionContext = null)
        val set = AdsFilterSet(listOf(filter1, filter2), logger)
        val result = set.filter(request, listOf(AdsEntity("1"), AdsEntity("2")), null)

        // THEN
        assertEquals(2, result.size)
        assertEquals(listOf(AdsEntity("1"), AdsEntity("2")), result)

        verify(filter1, never()).filter(any(), any(), anyOrNull())
        verify(filter2, never()).filter(any(), any(), anyOrNull())
    }
}