package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import com.wutsi.tracking.manager.util.URLUtil

class SourceFilter : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        // Query String
        if (track.url != null) {
            return track.copy(source = URLUtil.extractParams(track.url)["utm_source"])
        }

        // User Agent
        val ua = track.ua
        if (ua != null) {
            if (ua.contains("WhatsApp")) {
                return track.copy(source = "whatsapp")
            } else if (ua.contains("FBAN/Messenger") || ua.contains("FB_IAB/MESSENGER")) {
                return track.copy(source = "messenger")
            } else if (ua.contains("FBAN/FB")) {
                return track.copy(source = "facebook")
            } else if (ua.contains("TelegramBot (like TwitterBot)")) {
                return track.copy(source = "telegram")
            } else if (ua.contains("Twitter")) {
                return track.copy(source = "twitter")
            } else if (ua.contains("TikTok") || ua.contains("BytedanceWebview")) {
                return track.copy(source = "tiktok")
            }
        }

        return track
    }
}
