package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.enums.ChannelType
import com.wutsi.tracking.manager.Fixtures
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
        val track = createTrackEntity(referer = DailySourceMapper.EMAIL_REFERER)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.EMAIL, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://www.wutsi.com/read/123?utm_source=email",
            "https://www.wutsi.com/read/123?utm_medium=email",
        ],
    )
    fun emailFromUrl(url: String) {
        val track = createTrackEntity(url = url)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.EMAIL, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://www.wutsi.com/read/123?utm_source=email",
            "https://www.wutsi.com/read/123?utm_medium=email",
        ],
    )
    fun emailFromReferer(referer: String) {
        val track = createTrackEntity(referer = referer)
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
    fun reddit() {
        val track = createTrackEntity(channel = ChannelType.SOCIAL, referer = "https://m.reddit.com")
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.REDDIT, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun direct() {
        val track = createTrackEntity(channel = ChannelType.WEB, referer = null)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.DIRECT, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun directFacebookFromURL() {
        val track = createTrackEntity(
            channel = ChannelType.WEB,
            referer = null,
            url = "https://www.wutsi.com/read/123?fbclid=32093209",
        )
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.FACEBOOK, result.key.source)
        assertEquals(1L, result.value)
    }

    @Test
    fun directFacebookFromReferer() {
        val track = createTrackEntity(
            channel = ChannelType.WEB,
            referer = "https://www.wutsi.com/read/123?fbclid=32093209",
        )
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.FACEBOOK, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (Linux; Android 7.0; VS987 Build/NRD90U; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36 [FB_IAB/MESSENGER;FBAV/112.0.0.17.70;]",
            "Mozilla/5.0 (iPad; CPU OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) Mobile/14B100 [FBAN/MessengerForiOS;FBAV/122.0.0.40.69;FBBV/61279955;FBDV/iPad4,1;FBMD/iPad;FBSN/iOS;FBSV/10.1.1;FBSS/2;FBCR/;FBID/tablet;FBLC/vi_VN;FBOP/5;FBRV/0]",
        ],
    )
    fun messengerFromUA(ua: String) {
        val track = createTrackEntity(channel = ChannelType.MESSAGING, ua = ua)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.MESSENGER, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36 TelegramBot (like TwitterBot)",
        ],
    )
    fun telegramFromUA(ua: String) {
        val track = createTrackEntity(channel = ChannelType.MESSAGING, ua = ua)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.TELEGRAM, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/14F89 Twitter for iPhone/7.21.1",
        ],
    )
    fun twitterFromUA(ua: String) {
        val track = createTrackEntity(channel = ChannelType.SOCIAL, ua = ua)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.TWITTER, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://t.co", "https://www.twitter.com"])
    fun twitterFromReferer(referer: String) {
        val track = createTrackEntity(channel = ChannelType.SOCIAL, referer = referer)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.TWITTER, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["WhatsApp/2.21.19.21 A"])
    fun whatsappFromUA(ua: String) {
        val track = createTrackEntity(channel = ChannelType.MESSAGING, ua = ua)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.WHATSAPP, result.key.source)
        assertEquals(1L, result.value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://wa.me", "https://www.whatsapp.com"])
    fun whatsappFromReferer(referer: String) {
        val track = createTrackEntity(channel = ChannelType.MESSAGING, referer = referer)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.WHATSAPP, result.key.source)
        assertEquals(1L, result.value)
    }

    private fun createTrackEntity(
        channel: ChannelType? = null,
        source: String? = null,
        referer: String? = null,
        url: String = "https://www.wutso.com/read/123/test",
        ua: String? = null,
    ) = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
        url = url,
        referer = referer,
        source = source,
        channel = channel,
        ua = ua,
    )
}
