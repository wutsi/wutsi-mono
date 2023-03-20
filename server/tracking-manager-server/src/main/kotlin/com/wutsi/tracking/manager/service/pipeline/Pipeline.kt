package com.wutsi.tracking.manager.service.pipeline

import com.wutsi.tracking.manager.entity.TrackEntity

open class Pipeline(val steps: List<Filter>) : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        var cur = track
        steps.forEach {
            cur = it.filter(cur)
        }
        return cur
    }
}
