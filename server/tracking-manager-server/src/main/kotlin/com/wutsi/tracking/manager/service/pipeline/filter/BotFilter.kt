package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import ua_parser.Parser

open class BotFilter() : Filter {
    private val uaParser = Parser()

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ua == null) {
            return track
        }
        val client = uaParser.parse(track.ua)
        val bot = client.device?.family.equals("spider", true)
        return track.copy(bot = bot)
    }
}
