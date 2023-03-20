package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.TrackEntity

interface TrackFilter {
    fun accept(track: TrackEntity): Boolean
}
