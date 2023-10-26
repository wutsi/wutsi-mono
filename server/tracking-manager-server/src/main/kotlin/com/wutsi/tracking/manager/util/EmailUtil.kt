package com.wutsi.tracking.manager.util

import com.wutsi.tracking.manager.entity.TrackEntity

object EmailUtil {
    fun isImageProxy(track: TrackEntity): Boolean =
        track.ua?.contains("GoogleImageProxy") == true ||
            track.ua?.contains("YahooMailProxy") == true
}
