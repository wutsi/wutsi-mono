package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BotFilterTest {
    private val filter = BotFilter()

    @Test
    fun web() {
        val track = createTrack("Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)")
        assertFalse(filter.filter(track).bot)
    }

    @Test
    fun app() {
        val track = createTrack("Dart/2.16 (dart:io)")
        assertFalse(filter.filter(track).bot)
    }

    @Test
    fun bot() {
        val track = createTrack("Googlebot/2.1 (+http://www.google.com/bot.html)")
        assertTrue(filter.filter(track).bot)
    }

    @Test
    fun googleImage() {
        val track =
            createTrack("Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)")
        assertFalse(filter.filter(track).bot)
    }

    @Test
    fun noUserAgent() {
        val track = createTrack(null)
        assertFalse(filter.filter(track).bot)
    }

    private fun createTrack(ua: String?) = TrackEntity(
        ua = ua,
        bot = false,
    )
}
