package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.dao.TrackFilter
import com.wutsi.tracking.manager.entity.TrackEntity

interface Mapper<K, V> : TrackFilter {
    fun map(track: TrackEntity): KeyPair<K, V>?
}
