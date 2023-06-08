package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.TrackEntity

@Deprecated("")
interface TrackFilter {
    fun accept(track: TrackEntity): Boolean
}
