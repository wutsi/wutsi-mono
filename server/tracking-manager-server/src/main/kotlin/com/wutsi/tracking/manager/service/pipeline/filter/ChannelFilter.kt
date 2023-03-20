package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.enums.ChannelType
import com.wutsi.enums.util.ChannelDetector
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter

class ChannelFilter(private val detector: ChannelDetector) : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        val channel = track.url?.let {
            detector.detect(
                url = it,
                referer = track.referrer ?: "",
                ua = track.ua ?: "",
            )
        } ?: ChannelType.UNKNOWN

        return track.copy(channel = channel.name)
    }
}
