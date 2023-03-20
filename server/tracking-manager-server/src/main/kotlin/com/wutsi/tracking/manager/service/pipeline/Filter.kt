package com.wutsi.tracking.manager.service.pipeline

import com.wutsi.tracking.manager.entity.TrackEntity

interface Filter {
    fun filter(track: TrackEntity): TrackEntity
}
