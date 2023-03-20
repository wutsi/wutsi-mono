package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class SourceFilterTest {
    private val filter = SourceFilter()

    @Test
    fun `null`() {
        val track = filter.filter(createTrack(null))
        assertNull(track.source)
    }

    @Test
    fun empty() {
        val track = filter.filter(createTrack(""))
        assertNull(track.source)
    }

    @Test
    fun utmSource() {
        val track = filter.filter(createTrack("https://www.f.com?utm_source=foo"))
        assertEquals("foo", track.source)
    }

    @Test
    fun uaTwitter() {
        val track = filter.filter(
            createTrack(
                null,
                "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/14F89 Twitter for iPhone/7.21.1",
            ),
        )
        assertEquals("twitter", track.source)
    }

    @Test
    fun uaFacebook() {
        val track = filter.filter(
            createTrack(
                null,
                "Mozilla/5.0 (iPhone; CPU iPhone OS 16_1_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/20B101 [FBAN/FBIOS;FBDV/iPhone12,1;FBMD/iPhone;FBSN/iOS;FBSV/16.1.1;FBSS/2;FBID/phone;FBLC/en_US;FBOP/5]",
            ),
        )
        assertEquals("facebook", track.source)
    }

    @Test
    fun uaMessengerIOS() {
        val track = filter.filter(
            createTrack(
                null,
                "Mozilla/5.0 (iPad; CPU OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) Mobile/14B100 [FBAN/MessengerForiOS;FBAV/122.0.0.40.69;FBBV/61279955;FBDV/iPad4,1;FBMD/iPad;FBSN/iOS;FBSV/10.1.1;FBSS/2;FBCR/;FBID/tablet;FBLC/vi_VN;FBOP/5;FBRV/0]",
            ),
        )
        assertEquals("messenger", track.source)
    }

    @Test
    fun uaMessengerAndroid() {
        val track = filter.filter(
            createTrack(
                null,
                "Mozilla/5.0 (Linux; Android 7.0; VS987 Build/NRD90U; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36 [FB_IAB/MESSENGER;FBAV/112.0.0.17.70;]",
            ),
        )
        assertEquals("messenger", track.source)
    }

    @Test
    fun uaWhatsapp() {
        val track = filter.filter(createTrack(null, "WhatsApp/2.21.19.21 A"))
        assertEquals("whatsapp", track.source)
    }

    @Test
    fun uiTelegramBot() {
        val track = filter.filter(
            createTrack(
                null,
                "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36 TelegramBot (like TwitterBot)\"",
            ),
        )
        assertEquals("telegram", track.source)
    }

    private fun createTrack(url: String?, ua: String? = null) = TrackEntity(
        url = url,
        ua = ua,
    )
}
