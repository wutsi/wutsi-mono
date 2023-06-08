package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.entity.TrackEntity

interface Filter {
    fun accept(track: TrackEntity): Boolean
}
