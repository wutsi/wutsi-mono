package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.dto.ChannelType
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.util.EmailUtil
import org.springframework.stereotype.Service

@Service
class TrafficSourceDetector {
    companion object {
        const val FACEBOOK_PARAM = "fbclid"
        const val EMAIL_REFERER = "pixel.mail.wutsi.com"
    }

    fun detect(track: TrackEntity): TrafficSource {
        val ua = track.ua?.lowercase()
        val referer = track.referrer?.lowercase()
        val url = track.url?.lowercase()

        return if (
            referer == EMAIL_REFERER ||
            EmailUtil.isImageProxy(track) ||
            url?.contains("utm_medium=email") == true ||
            referer?.contains("utm_medium=email") == true ||
            url?.contains("utm_source=email") == true ||
            referer?.contains("utm_source=email") == true
        ) {
            TrafficSource.EMAIL
        } else if (
            ua?.contains("fban/fb") == true ||
            referer?.contains("facebook.com") == true ||
            url?.contains(FACEBOOK_PARAM) == true ||
            referer?.contains(FACEBOOK_PARAM) == true
        ) {
            TrafficSource.FACEBOOK
        } else if (
            (ua?.contains("twitter") == true && ua?.contains("telegrambot") == false) ||
            referer?.endsWith("t.co") == true ||
            referer?.contains("twitter.com") == true
        ) {
            TrafficSource.TWITTER
        } else if (
            ua?.contains("whatsapp") == true ||
            referer?.contains("wa.me") == true ||
            referer?.contains("whatsapp.com") == true
        ) {
            return TrafficSource.WHATSAPP
        } else if (
            ua?.contains("fban/messenger") == true ||
            ua?.contains("fb_iab/messenger") == true
        ) {
            TrafficSource.MESSENGER
        } else if (ua?.contains("telegrambot (like twitterbot)") == true) {
            TrafficSource.TELEGRAM
        } else if (track.channel == ChannelType.SEO.name) {
            TrafficSource.SEARCH_ENGINE
        } else if (referer.isNullOrEmpty()) {
            TrafficSource.DIRECT
        } else if (referer?.contains("reddit.com") == true) {
            TrafficSource.REDDIT
        } else if (referer?.contains("linkedin.com") == true) {
            TrafficSource.LINKEDIN
        } else {
            TrafficSource.UNKNOWN
        }
    }
}
