package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.enums.DeviceType
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import ua_parser.Parser

class DeviceTypeFilter : Filter {
    private val uaParser = Parser()

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ua == null) {
            return track
        }

        val deviceType = detect(track.ua)
        return track.copy(deviceType = deviceType?.name)
    }

    private fun detect(ua: String): DeviceType? {
        if (ua.contains("(dart:io)", true)) {
            return DeviceType.MOBILE
        } else if (ua.lowercase().contains("tablet") || ua.lowercase().contains("ipad")) {
            return DeviceType.TABLET
        }

        val client = uaParser.parse(ua)
        return if (client.device?.family.equals("spider", true)) { // Bot
            null
        } else if (client.userAgent.family?.contains("mobile", true) == true) {
            DeviceType.MOBILE
        } else {
            DeviceType.DESKTOP
        }
    }
}
