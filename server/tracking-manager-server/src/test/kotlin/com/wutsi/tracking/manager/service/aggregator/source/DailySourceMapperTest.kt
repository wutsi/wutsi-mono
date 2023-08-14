package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.enums.ChannelType
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.entity.TrafficSource
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.OffsetDateTime
import java.time.ZoneId

internal class DailySourceMapperTest {
    private val mapper = DailySourceMapper()

    @Test
    fun seo() {
        val track = createTrackEntity(channel = ChannelType.SEO)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.SEARCH_ENGINE, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun email() {
        val track = createTrackEntity(channel = ChannelType.EMAIL)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.EMAIL, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun facebook() {
        val track = createTrackEntity(channel = ChannelType.SOCIAL, referer = "https://l.facebook.com")
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.FACEBOOK, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun direct() {
        val track = createTrackEntity(channel = null)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.DIRECT, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://t.com", "https://www.twitter.com"])
    fun twitter(referer: String) {
        val track = createTrackEntity(channel = ChannelType.SOCIAL, referer = referer)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.TWITTER, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://wa.me", "https://www.whatsapp.com"])
    fun whatsapp(referer: String) {
        val track = createTrackEntity(channel = ChannelType.MESSAGING, referer = "https://wa.me")
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.WHATSAPP, result.key.source)
        assertEquals(1L, result.value)
    }

    private fun createTrackEntity(
        channel: ChannelType?,
        source: String? = null,
        referer: String? = null,
    ) = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
        referer = referer,
        source = source,
        channel = channel,
    )
}
