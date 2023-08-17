package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.enums.ChannelType
import com.wutsi.enums.util.ChannelDetector
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailySourceMapper : Mapper<TrackEntity, SourceKey, Long> {
    companion object {
        const val FACEBOOK_PARAM = "fbclid"
    }

    private val detector = ChannelDetector()

    override fun map(track: TrackEntity): KeyPair<SourceKey, Long> =
        SourceValue(
            SourceKey(track.productId!!, getSource(track)),
            1,
        )

    private fun getSource(track: TrackEntity): TrafficSource =
        when (getChannel(track)) {
            ChannelType.SEO.name -> TrafficSource.SEARCH_ENGINE
            ChannelType.EMAIL.name -> TrafficSource.EMAIL
            ChannelType.SOCIAL.name -> getSocialTraffic(track)
            ChannelType.MESSAGING.name -> getMessengerTraffic(track)
            else -> getDirectTraffic(track)
        }

    private fun getChannel(track: TrackEntity): String =
        if (track.channel.isNullOrEmpty()) {
            detector.detect(
                url = track.url ?: "",
                referer = track.referrer ?: "",
                ua = track.ua ?: "",
            ).name
        } else {
            track.channel
        }

    private fun getDirectTraffic(track: TrackEntity): TrafficSource {
        val url = track.url
        val referer = track.referrer
        return if (url?.contains(FACEBOOK_PARAM) == true || referer?.contains(FACEBOOK_PARAM) == true) {
            TrafficSource.FACEBOOK
        } else {
            TrafficSource.DIRECT
        }
    }

    private fun getSocialTraffic(track: TrackEntity): TrafficSource {
        val ua = track.ua?.lowercase()
        val referer = track.referrer?.lowercase()
        return if (
            ua?.contains("fban/fb") == true ||
            referer?.contains("facebook.com") == true
        ) {
            TrafficSource.FACEBOOK
        } else if (
            ua?.contains("twitter") == true ||
            referer?.contains("t.co") == true ||
            referer?.contains("twitter.com") == true
        ) {
            TrafficSource.TWITTER
        } else {
            TrafficSource.UNKNOWN
        }
    }

    private fun getMessengerTraffic(track: TrackEntity): TrafficSource {
        val ua = track.ua?.lowercase()
        val referer = track.referrer?.lowercase()
        return if (
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
        } else if (ua?.contains("telegrambot") == true) {
            TrafficSource.TELEGRAM
        } else {
            TrafficSource.UNKNOWN
        }
    }
}
