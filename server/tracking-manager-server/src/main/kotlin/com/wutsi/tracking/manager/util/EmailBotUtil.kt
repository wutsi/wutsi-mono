package com.wutsi.tracking.manager.util

import com.wutsi.tracking.manager.entity.TrackEntity

object EmailBotUtil {
    fun isBot(track: TrackEntity): Boolean =
        track.referrer?.contains("GoogleImageProxy") == true // This is for resolving the story pixel
}
