package com.wutsi.enum.util

import com.wutsi.enums.ChannelType
import com.wutsi.enums.util.ChannelDetector
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ChannelDetectorTest {
    companion object {
        const val UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)"
    }

    private val detector = ChannelDetector()

    private fun detect(
        url: String = "https://www.wutsi.com/p/1",
        referer: String = "http://www.googl.ecom",
        ua: String = UA,
    ) =
        detector.detect(url, referer, ua)

    @Test
    fun empty() {
        val channel = detect("", "", "")
        assertEquals(ChannelType.WEB, channel)
    }

    @Test
    fun app() {
        val channel = detect(ua = "Dart/2.16 (dart:io)")
        assertEquals(ChannelType.APP, channel)
    }

    @Test
    fun utmMedium() {
        val channel = detect(url = "https://www.wutsi.com?utm_medium=social&utm_source=facebook")
        assertEquals(ChannelType.SOCIAL, channel)
    }

    @Test
    fun refererEmail() {
        assertEquals(ChannelType.EMAIL, detect(referer = "https://mail.yahoo.com/m/120932"))
        assertEquals(ChannelType.EMAIL, detect(referer = "https://mail.google.com"))
        assertEquals(ChannelType.EMAIL, detect(referer = "https://outlook.live.com/m/120932"))
    }

    @Test
    fun whatsappReferer() {
        assertEquals(ChannelType.MESSAGING, detect(referer = "https://wa.me"))
        assertEquals(ChannelType.MESSAGING, detect(referer = "https://www.whatsapp.com"))
    }

    @Test
    fun twitterReferer() {
        assertEquals(ChannelType.SOCIAL, detect(referer = "https://www.twitter.com"))
        assertEquals(ChannelType.SOCIAL, detect(referer = "https://t.co"))
    }

    @Test
    fun twitterUA() {
        val channel =
            detect(ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/14F89 Twitter for iPhone/7.21.1")
        assertEquals(ChannelType.SOCIAL, channel)
    }

    @Test
    fun facebookReferer() {
        assertEquals(ChannelType.SOCIAL, detect(referer = "https://l.facebook.com"))
        assertEquals(ChannelType.SOCIAL, detect(referer = "https://www.facebook.com"))
    }

    @Test
    fun facebookUA() {
        val channel =
            detect(ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_1_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/20B101 [FBAN/FBIOS;FBDV/iPhone12,1;FBMD/iPhone;FBSN/iOS;FBSV/16.1.1;FBSS/2;FBID/phone;FBLC/en_US;FBOP/5]")
        assertEquals(ChannelType.SOCIAL, channel)
    }

    @Test
    fun seo() {
        assertEquals(ChannelType.SEO, detect(referer = "https://www.google.com"))
    }
}
