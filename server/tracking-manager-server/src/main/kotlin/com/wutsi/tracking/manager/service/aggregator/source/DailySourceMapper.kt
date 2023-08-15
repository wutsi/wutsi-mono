package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.enums.ChannelType
import com.wutsi.enums.util.ChannelDetector
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.entity.TrafficSource
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailySourceMapper : Mapper<TrackEntity, SourceKey, Long> {
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
            else -> TrafficSource.DIRECT
        }

    private fun getChannel(track: TrackEntity): String =
        if (track.channel.isNullOrEmpty()) {
            detector.detect(
                url = track.url ?: "",
                referer = track.referrer ?: "",
                ua = track.ua ?: ""
            ).name
        } else {
            track.channel
        }

    private fun getSocialTraffic(track: TrackEntity): TrafficSource {
        val referer = track.referrer?.lowercase()
        return if (referer?.contains("facebook.com") == true) {
            TrafficSource.FACEBOOK
        } else if (referer?.contains("t.co") == true || referer?.contains("twitter.com") == true) {
            TrafficSource.TWITTER
        } else {
            TrafficSource.UNKNOWN
        }
    }

    private fun getMessengerTraffic(track: TrackEntity): TrafficSource {
        val referer = track.referrer?.lowercase()
        return if (referer?.contains("wa.me") == true || referer?.contains("whatsapp.com") == true) {
            TrafficSource.WHATSAPP
        } else {
            TrafficSource.UNKNOWN
        }
    }
}
