package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import com.wutsi.tracking.manager.util.URLUtil

class CampaignFilter : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        if (track.url != null) {
            return track.copy(campaign = URLUtil.extractParams(track.url)["utm_campaign"])
        }
        return track
    }
}
