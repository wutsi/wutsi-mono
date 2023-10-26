package com.wutsi.tracking.manager.util

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.util.EmailUtil.isImageProxy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailUtilTest {
    @Test
    fun google() {
        assertTrue(isImageProxy(TrackEntity(ua = "Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)")))
    }

    @Test
    fun yahoo() {
        assertTrue(isImageProxy(TrackEntity(ua = "YahooMailProxy; https://help.yahoo.com/kb/yahoo-mail-proxy-SLN28749.html")))
    }

    @Test
    fun other() {
        assertFalse(isImageProxy(TrackEntity(ua = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.105 Safari/537.36")))
    }
}
