package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class DailyCampaignMapper : Mapper<TrackEntity, CampaignKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<CampaignKey, Long>> =
        listOf(
            CampaignValue(
                CampaignKey(track.campaign!!),
                1,
            ),
        )
}
