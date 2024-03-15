package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import com.wutsi.tracking.manager.service.aggregator.click.DailyClickFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyCampaignClickFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT = DailyClickFilter.EVENT
    }

    override fun accept(track: TrackEntity): Boolean =
        !track.bot &&
                track.event.equals(EVENT) &&
                !track.campaign.isNullOrEmpty() &&
                Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)
}
