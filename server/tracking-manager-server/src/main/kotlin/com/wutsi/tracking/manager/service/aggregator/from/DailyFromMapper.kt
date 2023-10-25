package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyFromMapper : Mapper<TrackEntity, FromKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<FromKey, Long>> =
        listOf(
            FromValue(
                FromKey(extractFrom(track.url!!)),
                1,
            )
        )

    private fun extractFrom(url: String): String {
        val prefix = "utm_from="
        val i = url.indexOf(prefix)
        if (i < 0) {
            return "DIRECT"
        }

        val j = url.indexOf("&", i + prefix.length)
        return if (j < 0) {
            url.substring(i + prefix.length)
        } else {
            url.substring(i + prefix.length, j)
        }
    }
}
