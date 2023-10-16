package com.wutsi.tracking.manager.service.pipeline.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.dto.IpApiResponse
import com.wutsi.tracking.manager.backend.IpApiBackend
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class CountryFilterTest {
    private val ipApi = mock<IpApiBackend>()
    private val filter = CountryFilter(ipApi)

    @Test
    fun filter() {
        doReturn(IpApiResponse(countryCode = "CM")).whenever(ipApi).resolve(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertEquals("CM", track.country)
    }

    @Test
    fun noIp() {
        val track = filter.filter(createTrack(ip = null))

        verify(ipApi, never()).resolve(any())
        assertNull(track.country)
    }

    @Test
    fun exception() {
        doThrow(RuntimeException::class).whenever(ipApi).resolve(any())

        val track = filter.filter(createTrack(ip = "10.2.100.100"))
        assertNull(track.country)
    }

    private fun createTrack(
        url: String? = null,
        referrer: String? = null,
        ua: String? = null,
        ip: String? = null,
    ) = TrackEntity(
        url = url,
        referrer = referrer,
        ua = ua,
        ip = ip,
    )
}
